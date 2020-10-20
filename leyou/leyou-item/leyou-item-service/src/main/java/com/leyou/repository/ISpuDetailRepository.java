package com.leyou.repository;

import com.leyou.item.pojo.SpuDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ISpuDetailRepository extends JpaSpecificationExecutor<SpuDetail>, JpaRepository<SpuDetail, Long> {
}
