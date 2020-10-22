package com.leyou.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.*;
import com.leyou.repository.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private ISpuRepository spuRepository;
    @Autowired
    private IBrandRepository brandRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ISpuDetailRepository spuDetailRepository;
    @Autowired
    private ISkuRepository skuRepository;
    @Autowired
    private IStockRepository stockRepository;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsService.class);

    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows, String sortBy, Boolean desc) {

        Specification<Spu> specification = new Specification<Spu>() {
            @Override
            public Predicate toPredicate(Root<Spu> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                //Búsqueda por palabras
                if (StringUtils.isNotBlank(key)) {
                    Predicate pTitle = criteriaBuilder.like(root.get("title"), "%" + key + "%");
                    predicateList.add(pTitle);
                }
                //alta o no
                if (saleable != null) {
                    Predicate pSaleable = criteriaBuilder.equal(root.get("saleable").as(Boolean.class), saleable);
                    predicateList.add(pSaleable);
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
        //pagination y sort
        Pageable pageable = null;
        if (StringUtils.isNotBlank(sortBy)) {
            Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortBy);
            pageable = PageRequest.of(page - 1, rows, sort);
        } else {
            pageable = PageRequest.of(page - 1, rows);
        }
        //Ejecutar la búsqueda a obtener el listado de Spu
        Page<Spu> spuPage = this.spuRepository.findAll(specification, pageable);
        List<Spu> spus = spuPage.getContent();
        //transferir la colección de Spu a SpuBo
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //Marca
            Optional<Brand> optionalBrand = this.brandRepository.findById(spu.getBrandId());
            if (optionalBrand.isPresent()) {
                Brand brand = optionalBrand.get();
                spuBo.setBname(brand.getName());
            }
            //Categoría
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));

            return spuBo;
        }).collect(Collectors.toList());
        //pageResult<spuBo>
        return new PageResult<>(spuPage.getTotalElements(), spuPage.getTotalPages(), spuBos);
    }

    /**
     * Añadir nuevo producto
     *
     * @param spuBo
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //1. añadir nuevo spu
        Spu spu = new Spu();
        BeanUtils.copyProperties(spuBo, spu);
        spu.setId(null);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spuBo.getCreateTime());
        spu.setSaleable(true);
        this.spuRepository.save(spu);
        //2. añadir spuDetail
        spuBo.setId(spu.getId());
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailRepository.save(spuDetail);
        System.out.println(spuBo);

        //añadir sku y stock
        saveSkuAndStock(spuBo);

        //rabbitMQ
        sendMsg("insert", spuBo.getId());
    }

    private void sendMsg(String type, Long id) {
        //enviar  mensaje
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        //añadir sku
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            if (sku.getEnable() == null) {
                sku.setEnable(false);
            }
            this.skuRepository.save(sku);
            //añadir stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockRepository.save(stock);
        });
    }

    /**
     * spuId -> spuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        Optional<SpuDetail> optionalSpuDetail = this.spuDetailRepository.findById(spuId);
        return optionalSpuDetail.orElse(null);
    }

    /**
     * spuId -> listado de sku + stock de cada sku
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        List<Sku> skus = this.skuRepository.findBySpuId(spuId);
        skus.forEach(sku -> {
            Optional<Stock> optionalStock = this.stockRepository.findById(sku.getId());
            if (optionalStock.isPresent()) {
                Stock stock = optionalStock.get();
                sku.setStock(stock.getStock());
            }
        });
        return skus;
    }

    /**
     * Actualizar info. del producto
     * primero eliminar los datos de producto antiguo y luego añadirlo de nuevo
     *
     * @param spuBo
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public void updateGoods(SpuBo spuBo) {
        //spuId-> listado de sku que se va a eliminar
        List<Sku> skus = this.skuRepository.findBySpuId(spuBo.getId());
        skus.forEach(sku -> {
            //eliminar datos de stock
            this.stockRepository.deleteById(sku.getId());
            //eliminar datos de sku
            this.skuRepository.delete(sku);
        });
        //añadir sku y stock
        saveSkuAndStock(spuBo);
        //actualizar spu y spuDetail
        Spu spu = new Spu();
        BeanUtils.copyProperties(spuBo, spu);
        spu.setLastUpdateTime(new Date());
        this.spuRepository.save(spu);
        SpuDetail spuDetail = spuBo.getSpuDetail();
        this.spuDetailRepository.save(spuDetail);
        //RabbitMQ
        sendMsg("update", spuBo.getId());
    }

    public Spu queryById(Long id) {
        return this.spuRepository.findById(id).orElse(null);
    }

    public Sku querySkuBySkuId(Long skuId) {
        return this.skuRepository.findById(skuId).orElse(null);
    }

    /**
     * Eliminar producto
     *
     * @param id
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public void deleteGoods(Long id) {
        List<Sku> skus = this.skuRepository.findBySpuId(id);
        for (Sku sku : skus) {
            this.stockRepository.deleteById(sku.getId());
            this.skuRepository.deleteById(sku.getId());
        }
        this.spuDetailRepository.deleteById(id);
        this.spuRepository.deleteById(id);

        sendMsg("delete", id);
    }

    /**
     * marcar un spu y sku bajo el mismo spu disponible or indisponible
     *
     * @param id
     */
    @Transactional(rollbackOn = Exception.class)
    public void changeSaleable(Long id) {
        Optional<Spu> optionalSpu = this.spuRepository.findById(id);
        if (optionalSpu.isPresent()) {
            Spu spu = optionalSpu.get();
            List<Sku> skus = this.skuRepository.findBySpuId(id);
            if (spu.getSaleable()) {
                skus.forEach(sku -> {
                    sku.setEnable(false);
                    this.skuRepository.save(sku);
                });
                spu.setSaleable(false);
                this.spuRepository.save(spu);
                sendMsg("delete", id);
            } else {
                skus.forEach(sku -> {
                    sku.setEnable(true);
                    this.skuRepository.save(sku);
                    spu.setSaleable(true);
                    this.spuRepository.save(spu);
                });
                sendMsg("update", id);
            }
        }

    }
}
