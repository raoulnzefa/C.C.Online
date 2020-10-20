package com.leyou.service;

import com.leyou.item.pojo.Category;
import com.leyou.repository.ICategoryRepository;
import com.sun.org.apache.bcel.internal.generic.ATHROW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    /**
     * Busca listado de catálogo por parent-id
     *
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        return this.categoryRepository.findByParentId(pid);
    }

    /**
     * listado de id de catálogo -> listado de nombre de catálogo
     * @param ids
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = this.categoryRepository.findAllById(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    /**
     * añadir un nuevo catálogo
     * @param category
     */
    public void saveCategory(Category category) {
        //1. ajusta el id= null
        category.setId(null);
        //2. guardar
        this.categoryRepository.save(category);
        //3. Editar el parent id = true
        Optional<Category> optionalCategory = this.categoryRepository.findById((category.getParentId()));
        if (optionalCategory.isPresent()) {
            Category parent = optionalCategory.get();
            parent.setIsParent(true);
            this.categoryRepository.save(parent);
        }
    }

    public void updateCategory(Category category) {
        Category currentCategory = this.categoryRepository.getOne(category.getId());
        currentCategory.setName(category.getName());
        this.categoryRepository.save(currentCategory);
    }

    public void deleteCategory(Long id) {
        Category category = this.categoryRepository.getOne(id);
        if (category.getIsParent()) {
            List<Category> leafList = new ArrayList<>();
            queryAllLeafNode(category, leafList);

            List<Category> list = new ArrayList<>();
            queryAllNode(category, list);

            for (Category c : list) {
                this.categoryRepository.delete(c);
            }

            for (Category c : leafList) {
                this.categoryRepository.deleteByCategoryIdInCategoryBrand(c.getId());
            }
        } else {
            List<Category> list = this.categoryRepository.findByParentId(category.getParentId());
            if (list.size() != 1) {
                this.categoryRepository.deleteById(category.getId());
                this.categoryRepository.deleteByCategoryIdInCategoryBrand(category.getId());
            } else {
                this.categoryRepository.deleteById(category.getId());
                Optional<Category> optionalCategory = this.categoryRepository.findById(category.getParentId());
                if (optionalCategory.isPresent()) {
                    Category parent = optionalCategory.get();
                    parent.setIsParent(false);
                    this.categoryRepository.save(parent);
                }
                this.categoryRepository.deleteByCategoryIdInCategoryBrand(category.getId());
            }
        }
    }

    /**
     * Listado de children catálogo para mantener la tabla de tb_category_brand
     * @param category
     * @param list
     */
    private void queryAllNode(Category category, List<Category> list) {
        list.add(category);
        List<Category> categories = this.categoryRepository.findByParentId(category.getId());

        for (Category category1 : categories) {
            queryAllNode(category1, list);
        }
    }

    private void queryAllLeafNode(Category category, List<Category> leafList) {
        if (!category.getIsParent()) {
            leafList.add(category);
        }
        List<Category> categories = this.categoryRepository.findByParentId(category.getId());

        for (Category category1 : categories) {
            queryAllLeafNode(category1, leafList);
        }
    }

    /**
     * al actualizar marca, muestra el listado de catálogos
     * @param bid
     * @return
     */
    public List<Category> queryCategoriesByBrandId(Long bid) {
        return this.categoryRepository.findByBrandId(bid);
    }

    /**
     * cid3 -> listado de catálogo de  cid1,2,3
     * @param cid3
     * @return
     */
    public List<Category> queryAllByCid3(Long cid3) {
        Optional<Category> optionalCategory3 = this.categoryRepository.findById(cid3);
        if (optionalCategory3.isPresent()) {
            Category c3 = optionalCategory3.get();
            Optional<Category> optionalCategory2 = this.categoryRepository.findById(c3.getParentId());
            if (optionalCategory2.isPresent()) {
                Category c2 = optionalCategory2.get();
                Optional<Category> optionalCategory1 = this.categoryRepository.findById(c2.getParentId());
                Category c1 = optionalCategory1.orElse(null);
                return Arrays.asList(c1, c2, c3);
            }
        }
        return null;
    }
}
