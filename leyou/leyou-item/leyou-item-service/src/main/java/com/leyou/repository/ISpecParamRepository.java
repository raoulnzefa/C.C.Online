package com.leyou.repository;

import com.leyou.item.pojo.SpecParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ISpecParamRepository extends JpaRepository<SpecParam, Long>, JpaSpecificationExecutor<SpecParam> {
    List<SpecParam> findByGroupId(Long gid);
}
