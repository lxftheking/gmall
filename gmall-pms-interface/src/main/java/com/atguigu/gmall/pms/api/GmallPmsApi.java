package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVo;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface GmallPmsApi {

    @PostMapping("pms/spuinfo/page")
    public Resp<List<SpuInfoEntity>> querySpuByPage(@RequestBody QueryCondition queryCondition);

    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId);
    @ApiOperation("详情查询")
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

        @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);


    @GetMapping("/pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryById1(@PathVariable("catId") Long catId);

    @GetMapping("/pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrValue(@PathVariable("spuId")Long spuId);


    @GetMapping("/pms/category")
    public Resp<List<CategoryEntity>> queryLevelOrCid(@RequestParam(value = "level", defaultValue = "0") Integer level,
                                                      @RequestParam(value = "parentCid", required = false) Long parentCid);
    @GetMapping("/pms/category/{pid}")
    public Resp<List<CategoryVo>>queryCategoryWithSub(@PathVariable("pid")Long pid);

    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<SkuImagesEntity>>queryImagesBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> querySpuDescBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/withattrvalues")
    public Resp<List<ItemGroupVO>>queryItemGroupVOsByCidAndSpuId(
            @RequestParam("cate")Long cid,
            @RequestParam("spuId")Long spuId
    );

    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>>querySaleAttrValueBySpuId(@PathVariable("spuId")Long spuId);

}
