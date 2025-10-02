package com.example.dao;

import com.example.model.Human;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class HumanDAO extends GenericDAO<Human> {
    public HumanDAO() {
        super(Human.class);
    }

    public List<Human> findAll() {
        return em.createQuery("SELECT h FROM Human h ORDER BY h.name", Human.class)
                .getResultList();
    }

    public List<Human> findByName(String name) {
        return em.createQuery("SELECT h FROM Human h WHERE h.name LIKE :name ORDER BY h.name", Human.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
}