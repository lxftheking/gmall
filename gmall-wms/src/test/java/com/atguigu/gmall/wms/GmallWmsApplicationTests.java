package com.atguigu.gmall.wms;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GmallWmsApplicationTests {

    @Autowired
    WareSkuDao wareSkuDao;
    @Test
    void contextLoads() {
        List<WareSkuEntity> check = wareSkuDao.check(28L, 5);
    }

}
