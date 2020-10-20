package com.leyou.repository;

import com.leyou.item.pojo.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IStockRepository extends JpaSpecificationExecutor<Stock>, JpaRepository<Stock, Long> {
}
