package com.example.dao;

import com.example.model.City;
import com.example.model.Climate;
import com.example.model.Coordinates;
import com.example.model.Human;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Stateless
public class CityDAO extends GenericDAO<City> {
    public CityDAO() {
        super(City.class);
    }

    public void deleteCityWithRelations(Integer cityId) {
        try {
            City city = find(cityId);
            if (city == null) {
                System.out.println("Город с ID " + cityId + " не найден");
                return;
            }

            System.out.println("🗑 Удаление города ID: " + cityId + " - " + city.getName());
            Long coordinatesId = city.getCoordinates() != null ? city.getCoordinates().getId() : null;
            Long governorId = city.getGovernor() != null ? city.getGovernor().getId() : null;
            String governorName = city.getGovernor() != null ? city.getGovernor().getName() : "null";
            delete(city);
            System.out.println("Город удален");
            if (governorId != null) {
                Long remainingCities = em.createQuery(
                                "SELECT COUNT(c) FROM City c WHERE c.governor.id = :governorId", Long.class)
                        .setParameter("governorId", governorId)
                        .getSingleResult();

                System.out.println("Губернатор " + governorName + " используется в " + remainingCities + " городах");

                if (remainingCities == 0) {
                    Human governor = em.find(Human.class, governorId);
                    if (governor != null) {
                        em.remove(governor);
                        System.out.println("Губернатор удален (больше не используется)");
                    }
                } else {
                    System.out.println("Губернатор оставлен (используется другими городами)");
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при удалении города с связями: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public List<City> findLazy(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<City> cq = cb.createQuery(City.class);
        Root<City> root = cq.from(City.class);

        // Применяем фильтры
        Predicate filterPredicate = buildFilters(cb, root, filterBy);
        if (filterPredicate != null) {
            cq.where(filterPredicate);
        }

        // Применяем сортировку
        if (sortBy != null && !sortBy.isEmpty()) {
            List<Order> orders = new ArrayList<>();
            for (SortMeta sortMeta : sortBy.values()) {
                String field = sortMeta.getField();
                boolean ascending = sortMeta.getOrder().isAscending();

                Path<?> path = getPath(root, field);
                orders.add(ascending ? cb.asc(path) : cb.desc(path));
            }
            cq.orderBy(orders);
        } else {
            // Сортировка по умолчанию
            cq.orderBy(cb.asc(root.get("id")));
        }

        return em.createQuery(cq)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public int count(Map<String, FilterMeta> filterBy) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<City> root = cq.from(City.class);

        cq.select(cb.count(root));

        Predicate filterPredicate = buildFilters(cb, root, filterBy);
        if (filterPredicate != null) {
            cq.where(filterPredicate);
        }

        return em.createQuery(cq).getSingleResult().intValue();
    }

    private Predicate buildFilters(CriteriaBuilder cb, Root<City> root, Map<String, FilterMeta> filterBy) {
        if (filterBy == null || filterBy.isEmpty()) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        for (FilterMeta filter : filterBy.values()) {
            String field = filter.getField();
            Object value = filter.getFilterValue();

            if (value != null && !value.toString().trim().isEmpty()) {
                Path<?> path = getPath(root, field);

                // Для числовых полей преобразуем в строку для поиска
                if (path.getJavaType().equals(String.class)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"));
                } else {
                    // Для нестроковых полей преобразуем в строку для поиска
                    predicates.add(cb.like(cb.lower(path.as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"));
                }
            }
        }

        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
    }

    private Path<?> getPath(Root<City> root, String field) {
        // Обработка вложенных полей
        if (field.contains(".")) {
            String[] parts = field.split("\\.");
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(field);
    }


    public void deleteOneByClimate(Climate climate) {
        List<City> cities = em.createQuery("SELECT c FROM City c WHERE c.climate = :climate", City.class)
                .setParameter("climate", climate)
                .setMaxResults(1)
                .getResultList();
        if (!cities.isEmpty()) {
            delete(cities.get(0));
        }
    }

    public Long getSumMetersAboveSeaLevel() {
        return em.createQuery("SELECT SUM(c.metersAboveSeaLevel) FROM City c", Long.class)
                .getSingleResult();
    }

    public List<City> findByNamePrefix(String prefix) {
        return em.createQuery("SELECT c FROM City c WHERE c.name LIKE :prefix", City.class)
                .setParameter("prefix", prefix + "%")
                .getResultList();
    }

    public City findMinPopulationCity() {
        return em.createQuery("SELECT c FROM City c ORDER BY c.population ASC", City.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    public City findMaxPopulationCity() {
        return em.createQuery("SELECT c FROM City c ORDER BY c.population DESC", City.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<City> findByGovernorId(Long governorId) {
        return em.createQuery("SELECT c FROM City c WHERE c.governor.id = :governorId", City.class)
                .setParameter("governorId", governorId)
                .getResultList();
    }

    public City find(Integer id) {
        try {
            // Используем JOIN FETCH для eager loading связанных объектов
            String jpql = "SELECT c FROM City c " +
                    "LEFT JOIN FETCH c.coordinates " +
                    "LEFT JOIN FETCH c.governor " +
                    "WHERE c.id = :id";

            return em.createQuery(jpql, City.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

public City createCityWithRelations(City city) {
    try {


        if (city.getCoordinates() != null && city.getCoordinates().getId() == null) {
            em.persist(city.getCoordinates());
            System.out.println("Новые координаты сохранены");
        }
        if (city.getGovernor() != null && city.getGovernor().getId() == null) {
            em.persist(city.getGovernor());
            System.out.println("Новый губернатор сохранен: " + city.getGovernor().getName());
        }

        em.persist(city);
        em.flush();

        System.out.println("ГОРОД УСПЕШНО СОЗДАН ID: " + city.getId());
        return city;
    } catch (Exception e) {
        System.err.println("ОШИБКА при создании города: " + e.getMessage());

        throw new RuntimeException("Не удалось создать город: " + e.getMessage());
    }
}
}