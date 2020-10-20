package com.leyou.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.repository.ISpecGroupRepository;
import com.leyou.repository.ISpecParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private ISpecGroupRepository specGroupRepository;
    @Autowired
    private ISpecParamRepository specParamRepository;

    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        return this.specGroupRepository.findByCid(cid);
    }
    /**
     * 根据条件查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        Specification<SpecParam> specification = new Specification<SpecParam>() {
            @Override
            public Predicate toPredicate(Root<SpecParam> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                if (gid != null) {
                    Predicate gid1 = criteriaBuilder.equal(root.get("groupId").as(Long.class), gid);
                    predicateList.add(gid1);
                }
                if (cid != null) {
                    Predicate cid1 = criteriaBuilder.equal(root.get("cid").as(Long.class), cid);
                    predicateList.add(cid1);
                }
                if (generic != null) {
                    Predicate generic1 = criteriaBuilder.equal(root.get("generic").as(Boolean.class), generic);
                    predicateList.add(generic1);
                }
                if (searching != null) {
                    Predicate searching1 = criteriaBuilder.equal(root.get("searching").as(Boolean.class), searching);
                    predicateList.add(searching1);
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
        return specParamRepository.findAll(specification);
    }

    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(group -> {
            List<SpecParam> params = this.queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }

    /**
     * añadir grupo de especificaciones
     * @param specGroup
     * @return
     */
    public void saveSpecGroup(SpecGroup specGroup) {
        specGroup.setId(null);
        this.specGroupRepository.save(specGroup);
    }

    public void updateSpecGroup(SpecGroup specGroup) {
        this.specGroupRepository.save(specGroup);
    }

    public void deleteSpecGroup(Long gid) {
        this.specGroupRepository.deleteById(gid);
    }

    public void saveSpecParam(SpecParam specParam) {
        specParam.setId(null);
        this.specParamRepository.save(specParam);
    }

    public void updateSpecParam(SpecParam specParam) {
        this.specParamRepository.save(specParam);
    }

    public void deleteSpecParam(Long pid) {
        this.specParamRepository.deleteById(pid);
    }
}
