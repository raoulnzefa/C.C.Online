package com.leyou.repository;

import com.leyou.item.pojo.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ISkuRepository extends JpaRepository<Sku, Long>, JpaSpecificationExecutor<Sku> {
    List<Sku> findBySpuId(Long spuId);
}
