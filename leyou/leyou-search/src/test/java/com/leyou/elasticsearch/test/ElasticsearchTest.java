package com.leyou.elasticsearch.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void testDelete() {
        this.elasticsearchTemplate.deleteIndex(Goods.class);
    }

    @Test
    public void testCreate() {
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);
    }

    //añadir datos a type goods
    @Test
    public void test() {

        Integer page = 1;
        Integer rows = 100;

        do {
            //consultar spuBo por página
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(null, true, page, rows, null, null);
            //sacar datos de página
            List<SpuBo> items = result.getItems();
            //LIst<SpuBo> ==> List<Goods>
            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    System.out.println("no se encuentra: " + spuBo.getId());;
                }
                return null;
            }).collect(Collectors.toList());

            //añadir
            this.goodsRepository.saveAll(goodsList);
            rows = items.size();
            page++;

        } while (rows == 100);
    }
}
