package com.atguigu.gmall.cart.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
public class CartListener {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CURRENT_PRICE_PREFIX = "cart:price";

    private static final String KEY_PREFIX="cart:item:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "CART-UPDATE-QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",ignoreDeclarationExceptions ="true", type = ExchangeTypes.TOPIC),
            key = {"cart.update"}
    ))
    public void listener(Long spuId){
        Resp<List<SkuInfoEntity>> listResp = pmsClient.querySkuBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = listResp.getData();
        if(!CollectionUtils.isEmpty(skuInfoEntities)){
            skuInfoEntities.forEach(skuInfoEntity -> {
                redisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX+skuInfoEntity.getSkuId(),skuInfoEntity.getPrice().toString());
            });
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "CART-DELETE-QUEUE", durable = "true"),
            exchange = @Exchange(value = "ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))
    public void deleteListener(Map<String,Object>map){
        Long userId = (Long)map.get("userId");
        String skuIds = (String)map.get("skuIds");
        System.out.println(skuIds);
        if (StringUtils.isEmpty(skuIds)){
            return;
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(KEY_PREFIX + userId);
        List<String> list = JSON.parseArray(skuIds, String.class);

        hashOps.delete(list.toArray());

    }
}
