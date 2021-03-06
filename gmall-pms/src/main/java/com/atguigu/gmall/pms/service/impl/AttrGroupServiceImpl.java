package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.Vo.GroupVo;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
     private ProductAttrValueDao productAttrValueDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryGroupByCid(QueryCondition queryCondition, Long catId) {

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId)
        );
        return new PageVo(page);
    }

    @Override
    public GroupVo queryGroupVoByGid(Long gid) {
        //根据id查询组
        GroupVo groupVo = new GroupVo();
        AttrGroupEntity groupEntity = this.getById(gid);
        BeanUtils.copyProperties(groupEntity,groupVo);


        //查询中间表
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",groupVo.getAttrGroupId()));
        groupVo.setRelations(relationEntities);

        //查询规格参数,获取的是规格参数的ID
        List<Long> list1 = relationEntities.stream().map(relationEntity->relationEntity.getAttrId()).collect(Collectors.toList());

        //根据规格参数的ID查询某个规格参数的所有信息
//         = attrDao.selectBatchIds(list1);
        List<AttrEntity> list=new ArrayList<>();
        for (Long aLong : list1) {
            AttrEntity attrEntity = attrDao.selectById(aLong);
            list.add(attrEntity);
        }
        groupVo.setAttrEntities(list);


        return groupVo;
    }

    @Override
    public List<GroupVo> queryGroupVoById(Long catId) {

       //根据分类的id查询规格参数组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));


        //遍历规格参数组查询每个组下中间关系//查询每个组下的规格参数
       return  groupEntities.stream().map(attrGroupEntity -> this.queryGroupVoByGid(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());
    }

    @Override
    public List<ItemGroupVO> queryItemGroupVOsByCidAndSpuId(Long cid, Long spuId) {
        //根据sku中的categoryId查询分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        if(CollectionUtils.isEmpty(attrGroupEntities)){
            return  null;
        }
        List<ItemGroupVO> itemGroupVOS = attrGroupEntities.stream().map(group -> {
            ItemGroupVO itemGroupVO = new ItemGroupVO();
            itemGroupVO.setId(group.getCatelogId());
            itemGroupVO.setName(group.getAttrGroupName());
            //遍历组到中间表查询每个中的规格参数
            List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", group.getAttrGroupId()));
             if(!CollectionUtils.isEmpty(attrGroupEntities)){

                 List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

                 //根据spuId和attrID查询规格参数和值
                 List<ProductAttrValueEntity> attrValueEntities = productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));

                 itemGroupVO.setBaseAttrValues(attrValueEntities);
             }
            return itemGroupVO;
        }).collect(Collectors.toList());
        return itemGroupVOS;
    }

}