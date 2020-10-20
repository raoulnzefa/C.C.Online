package com.leyou.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.repository.IBrandRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;


@Service
public class BrandService {

    @Autowired
    private IBrandRepository brandRepository;

    /**
     * Según condiciones de búsqueda, saca un resultado de Objecto de PageResult
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        Specification<Brand> specification = new Specification<Brand>() {
            @Override
            public Predicate toPredicate(Root<Brand> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //List<Predicate> predicateList = new ArrayList<>();
                // búsqueda difusa según name e iniciales
                if (StringUtils.isNotBlank(key)) {
                    Path<Object> name = root.get("name");
                    Path<Object> letter = root.get("letter");
                    Predicate pName = criteriaBuilder.like(name.as(String.class), "%" + key + "%");
                    Predicate pLetter = criteriaBuilder.equal(letter, key);
                    return criteriaBuilder.or(pLetter, pName);
                    //predicateList.add(or);
                }
                //return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
                return null;
            }
        };
        //pagination y sort
        Pageable pageable = null;
        if (StringUtils.isNotBlank(sortBy)) {
            Sort.Direction direction = desc ? Direction.DESC : Direction.ASC;
            Sort sort = Sort.by(direction, sortBy);
            pageable = PageRequest.of(page - 1, rows, sort);
        } else {
            pageable = PageRequest.of(page - 1, rows);
        }
        //resultado de clase Page
        Page<Brand> brandPage = this.brandRepository.findAll(specification, pageable);
        //Encapsular PageResult
        return new PageResult<>(brandPage.getTotalElements(), brandPage.getTotalPages(), brandPage.getContent());
    }

    /**
     * añadir marca
     * @param brand
     * @param cids
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public void saveBrand(Brand brand, List<Long> cids) {
        //1.tabla brand
        this.brandRepository.save(brand);
        //2.tabla catálogo_brand
        cids.forEach(cid -> {
            this.brandRepository.insertCategoryAndBrand(cid, brand.getId());
        });
    }

    public List<Brand> queryBrandsByCid(Long cid) {
        return brandRepository.findBrandsByCid(cid);
    }

    public Brand queryBrandById(Long id) {
        return this.brandRepository.findById(id).orElse(null);
    }


    /**
     * actualizar marca
     * @param brand
     * @param cids
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) {
        this.brandRepository.deleteByBrandIdInCategoryBrand(brand.getId());
        this.brandRepository.save(brand);
        cids.forEach(cid -> {
            this.brandRepository.insertCategoryAndBrand(cid, brand.getId());
        });
    }

    /**
     * eliminar marca en tb_brand
     * @param bid
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public void deleteBrand(Long bid) {
        this.brandRepository.deleteById(bid);
        this.brandRepository.deleteByBrandIdInCategoryBrand(bid);
    }
}
