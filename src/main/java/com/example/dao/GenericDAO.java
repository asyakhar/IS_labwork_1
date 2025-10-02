//package com.example.dao;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import java.util.List;
//
//public abstract class GenericDAO<T> {
//
//    @PersistenceContext
//    protected EntityManager em;
//
//    private final Class<T> entityClass;
//
//    public GenericDAO(Class<T> entityClass) {
//        this.entityClass = entityClass;
//    }
//
//    public void create(T entity) {
//        em.persist(entity);
//    }
//
//    public T find(Object id) {
//        return em.find(entityClass, id);
//    }
//
//    public T update(T entity) {
//        return em.merge(entity);
//    }
//
//    public void delete(T entity) {
//        em.remove(em.merge(entity));
//    }
//    public void deleteById(Object id) {
//        T entity = find(id);
//        if (entity != null) {
//            delete(entity);
//        }
//    }
//    public List<T> findAll() {
//        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
//                .getResultList();
//    }
//
//}
package com.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

public abstract class GenericDAO<T> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;
    private static final Logger logger = Logger.getLogger(GenericDAO.class.getName());

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

public void create(T entity) {
    try {
        logger.info(() -> "Создание " + entityClass.getSimpleName());

        em.persist(entity);
        em.flush();

        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        logger.info(() -> "Успешно создано " + entityClass.getSimpleName() + " с id=" + id);
    } catch (Exception e) {

        logger.severe("Ошибка при создании " + entityClass.getSimpleName() + ": " + e.getMessage());
        throw new RuntimeException(e);
    }
}

    public T find(Object id) {
        logger.info(() -> "Поиск " + entityClass.getSimpleName() + " по id=" + id);
        return em.find(entityClass, id);
    }

    public T update(T entity) {
        try {
            T merged = em.merge(entity);
            logger.info(() -> "Entity " + entityClass.getSimpleName() + " обновлён: " + merged);
            return merged;
        } catch (Exception e) {
            logger.severe(() -> "Ошибка при обновлении " + entityClass.getSimpleName() + ": " + e.getMessage());
            throw e;
        }
    }

    public void delete(T entity) {
        try {
            em.remove(em.merge(entity));
            logger.info(() -> "Удалён " + entityClass.getSimpleName() + ": " + entity);
        } catch (Exception e) {
            logger.severe(() -> "Ошибка при удалении " + entityClass.getSimpleName() + ": " + e.getMessage());
            throw e;
        }
    }

    public void deleteById(Object id) {
        T entity = find(id);
        if (entity != null) {
            delete(entity);
        } else {
            logger.warning(() -> "Не найден " + entityClass.getSimpleName() + " для удаления id=" + id);
        }
    }

    public List<T> findAll() {
        logger.info(() -> "Получение всех записей " + entityClass.getSimpleName());
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }
}
