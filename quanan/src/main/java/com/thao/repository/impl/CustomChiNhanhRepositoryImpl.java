/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thao.repository.impl;

import com.thao.pojo.ChiNhanh;
import com.thao.repository.CustomChiNhanhRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Chung Vu
 */
@Repository
@PropertySource("classpath:configs.properties")
public class CustomChiNhanhRepositoryImpl implements CustomChiNhanhRepository {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Environment env;

    @Override
    public List<ChiNhanh> getChiNhanhTheoChuChiNhanh(int id) {
        CriteriaBuilder b = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChiNhanh> q = b.createQuery(ChiNhanh.class);
        Root root = q.from(ChiNhanh.class);
        q.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("idNguoiDung").get("id"), id));
        q.where(predicates.toArray(Predicate[]::new));

        Query query = entityManager.createQuery(q);
        

        return query.getResultList();
    }

    @Override
    public List<ChiNhanh> getChiNhanhs(Map<String, String> params) {
        CriteriaBuilder b = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChiNhanh> q = b.createQuery(ChiNhanh.class);
        Root root = q.from(ChiNhanh.class);
        q.select(root);
        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {

            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("diaChi"), String.format("%%%s%%", kw)));
            }
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = entityManager.createQuery(q);
        if (params != null) {
            String page = params.get("page");
            if (page != null) {
                int pageSize = Integer.parseInt(this.env.getProperty("PAGE_SIZE"));
                query.setFirstResult((Integer.parseInt(page) - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
        }

        return query.getResultList();
    }

}
