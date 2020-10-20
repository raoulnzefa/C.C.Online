package com.leyou.repository;

import com.leyou.item.pojo.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IBrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {

    /**
     * añadir marca y category_brand
     * @param cid category_id
     * @param id brand_id
     */
    @Query(value="insert into tb_category_brand (category_id, brand_id) values (?1, ?2) ", nativeQuery= true)
    @Modifying
    void insertCategoryAndBrand(Long cid, Long id);

    @Query(value="select * from tb_brand a inner join tb_category_brand b on a.id = b.brand_id where b.category_id = ?1  ", nativeQuery=true)
    List<Brand> findBrandsByCid(Long cid);

    @Query(value="delete from tb_category_brand where brand_id = ?1", nativeQuery = true)
    @Modifying
    void deleteByBrandIdInCategoryBrand(Long id);
}
