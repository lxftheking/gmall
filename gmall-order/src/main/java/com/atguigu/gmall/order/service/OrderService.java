package com.atguigu.gmall.order.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderItemVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Service
public class OrderService {


    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private GmallCartClient cartClient;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallWmsClient wmsClient;


    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


//    private ThreadPoolExecutor threadPoolExecutor= new ThreadPoolExecutor(500, 800, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000));
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private static final String TOKEN_PREFIX="order:token:";

    public OrderConfirmVo confirm() {

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //获取用户的地址信息，远程接口
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            Resp<List<MemberReceiveAddressEntity>> memberResp = umsClient.queryAddressesByUserId(userInfo.getUserId());
            List<MemberReceiveAddressEntity> memberReceiveAddressEntities = memberResp.getData();
            orderConfirmVo.setAddresses(memberReceiveAddressEntities);

        }, threadPoolExecutor);

        //获取订单的详情列表，远程接口
        //注意获取订单的信息，不能所有的数据都从redis中获取，要从数据库获取，数据以数据库为准
        //从redis中获取购物车中选中的商品的skuId和商品数量,然后从数据库中查询其他数据
        CompletableFuture<Void> orderFuture = CompletableFuture.supplyAsync(() -> {
            Resp<List<Cart>> cartResp = cartClient.queryCheckedCarts(userInfo.getUserId());
            return cartResp.getData();
        }, threadPoolExecutor).thenAcceptAsync(carts -> {
            List<OrderItemVo> orderItemVos = carts.stream().map(cart -> {
                Long skuId = cart.getSkuId();
                Integer count = cart.getCount();
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setCount(count);
                orderItemVo.setSkuId(skuId);

                //查询sku相关信息
                CompletableFuture<Void> skuFuture = CompletableFuture.runAsync(() -> {
                    Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(skuId);
                    SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                    if (skuInfoEntity != null) {
                        orderItemVo.setPrice(skuInfoEntity.getPrice());
                        orderItemVo.setSkuTitle(skuInfoEntity.getSkuTitle());
                        orderItemVo.setWeight(skuInfoEntity.getWeight());
                        orderItemVo.setImage(skuInfoEntity.getSkuDefaultImg());
                    }

                }, threadPoolExecutor);

                //查询商品的库存信息
                CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {

                    Resp<List<WareSkuEntity>> wareSkuResp = wmsClient.querySkuBySpuId(skuId);
                    List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                    if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                        orderItemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                    }
                }, threadPoolExecutor);

                //查询销售属性
                CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
                    Resp<List<SkuSaleAttrValueEntity>> saleAttrResp = pmsClient.querySaleAttrValueBySkuId(skuId);
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrResp.getData();

                    orderItemVo.setSaleAttrs(skuSaleAttrValueEntities);

                }, threadPoolExecutor);

                //查询营销信息
                CompletableFuture<Void> saleFuture = CompletableFuture.runAsync(() -> {
                    Resp<List<ItemSaleVO>> resp = smsClient.querySaleVoBySkuId(skuId);
                    List<ItemSaleVO> itemSaleVOS = resp.getData();
                    orderItemVo.setSales(itemSaleVOS);

                }, threadPoolExecutor);
                CompletableFuture.allOf(skuFuture, storeFuture, saleAttrFuture, saleFuture).join();
                return orderItemVo;
            }).collect(Collectors.toList());

            orderConfirmVo.setOrderItemVos(orderItemVos);
        }, threadPoolExecutor);


        //获取用户的积分信息，远程接口ums
        CompletableFuture<Void> boundFuture = CompletableFuture.runAsync(() -> {
            Resp<MemberEntity> memberEntityResp = umsClient.queryMemberById(userInfo.getUserId());
            MemberEntity memberEntity = memberEntityResp.getData();
            if (memberEntity != null) {
                orderConfirmVo.setBounds(memberEntity.getIntegration());
            }

        }, threadPoolExecutor);

        //防止重复提交的唯一标志
        //分布式ID生成器（mybites-plus中集成了一个）
        CompletableFuture<Void> tokenFuture = CompletableFuture.runAsync(() -> {
            String orderToken = IdWorker.getTimeId();
            orderConfirmVo.setOrderToken(orderToken);//浏览器一份

            //保存redis一份
            redisTemplate.opsForValue().set(TOKEN_PREFIX + orderToken, orderToken, 3, TimeUnit.HOURS);

        }, threadPoolExecutor);

        CompletableFuture.allOf(addressFuture,orderFuture,boundFuture,tokenFuture).join();

        return  orderConfirmVo;
    }
}
