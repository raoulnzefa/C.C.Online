package com.example.dawnelasticsearch;

import com.example.dawnelasticsearch.pojo.Item;
import com.example.dawnelasticsearch.repository.ItemRepository;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DawnElasticsearchApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemRepository itemRepository;

    //创建索引和映射
    @Test
    public void testCreate() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        this.elasticsearchTemplate.createIndex(Item.class);
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        this.elasticsearchTemplate.putMapping(Item.class);
    }

    //删除索引
    @Test
    public void testDelete() {
        this.elasticsearchTemplate.deleteIndex("heima");
    }

    //新增文档
    @Test
    public void index() {
        Item item = new Item(6L, "小米手机88", " 手机", "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        this.itemRepository.save(item);
    }

    //批量新增
    @Test
    public void indexList() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(7L, "坚果手机88", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(8L, "华为META108", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        this.itemRepository.saveAll(list);
    }

    //基本查询
    @Test
    public void testQuery() {
        Optional<Item> optionalItem = this.itemRepository.findById(8L);
        System.out.println(optionalItem.get());
    }
    //排序
    @Test
    public void testFind() {
        Iterable<Item> items = this.itemRepository.findAll(
                Sort.by(Sort.Direction.DESC, "price"));
        items.forEach(System.out::println);
    }

    //自定义方法
    @Test
    public void queryByPriceBetween() {
        List<Item> items = this.itemRepository.findByPriceBetween(2000.00, 3500.00);
        items.forEach(System.out::println);
    }

    //高级查询
    //基本查询
    @Test
    public void testQueryBuilder() {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "小米");
        Iterable<Item> items = this.itemRepository.search(queryBuilder);
        items.forEach(System.out::println);
    }
    //自定义查询
    @Test
    public void testNativeQuery() {
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本的分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米"));
        //执行搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        //打印总条数
        System.out.println(items.getTotalElements());
        //打印总页数
        System.out.println(items.getTotalPages());
        items.forEach(System.out::println);
    }
    //分页查询
    @Test
    public void testNativeQueryByPage() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        int page = 0;
        int size = 3;
        queryBuilder.withPageable(PageRequest.of(page, size));
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 打印总条数
        System.out.println(items.getTotalElements());
        // 打印总页数
        System.out.println(items.getTotalPages());
        // 每页大小
        System.out.println(items.getSize());
        // 当前页
        System.out.println(items.getNumber());
        items.forEach(System.out::println);
    }

    //排序
    @Test
    public void testSort() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        System.out.println(items.getTotalElements());
        items.forEach(System.out::println);
    }

    //聚合
    //聚合为桶
    @Test
    public void testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage =
                (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        //3.3 遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }

    //嵌套聚合，求平均值
    @Test
    public void testSubAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price"))// 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage aggPage = (AggregatedPage) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");
            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }

    }



}
