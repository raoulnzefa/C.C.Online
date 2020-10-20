package com.leyou.goodsTest;

import com.leyou.item.pojo.Spu;
import com.leyou.service.GoodsService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

    @Autowired
    private GoodsService goodsService;

    @org.junit.Test
    public void testSpu() {
        Spu spu =(Spu) this.goodsService.queryById((long) 2);
        System.out.println(spu);
    }
}
