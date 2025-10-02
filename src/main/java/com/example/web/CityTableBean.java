package com.example.web;

import com.example.dao.CityDAO;
import com.example.model.City;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Named("cityTableBean")
@SessionScoped
public class CityTableBean implements Serializable {

    @Inject
    private CityDAO cityDAO;

    private LazyDataModel<City> lazyModel;
    private List<City> filteredCities;

    public LazyDataModel<City> getLazyModel() {
        if (lazyModel == null) {
            lazyModel = new LazyDataModel<City>() {
                @Override
                public List<City> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                    try {
                        // Получаем данные с фильтрацией и сортировкой
                        List<City> result = cityDAO.findLazy(first, pageSize, sortBy, filterBy);

                        // Устанавливаем общее количество записей для пагинации
                        int count = cityDAO.count(filterBy);
                        this.setRowCount(count);

                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        return cityDAO.count(filterBy);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            };
        }
        return lazyModel;
    }

    public List<City> getFilteredCities() {
        return filteredCities;
    }

    public void setFilteredCities(List<City> filteredCities) {
        this.filteredCities = filteredCities;
    }

    public void clearFilters() {
        filteredCities = null;
    }
}