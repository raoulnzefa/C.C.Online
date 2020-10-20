package com.leyou.repository;

import com.leyou.item.pojo.SpecGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISpecGroupRepository extends JpaRepository<SpecGroup, Long>, JpaSpecificationExecutor<SpecGroup> {

    List<SpecGroup> findByCid(Long cid);
}
