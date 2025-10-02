package com.example.web;

import com.example.dao.CityDAO;
import com.example.model.City;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("cityViewBean")
@ViewScoped
public class CityViewBean implements Serializable {

    @Inject
    private CityDAO cityDAO;

    private City city;
    private Integer cityId; // ИЗМЕНИТЕ Long на Integer

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();

        String idParam = params.get("id");
        System.out.println("Received ID parameter: " + idParam);

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                this.cityId = Integer.parseInt(idParam); // ИЗМЕНИТЕ Long.parseLong на Integer.parseInt
                loadCity();
            } catch (NumberFormatException e) {
                System.err.println("Invalid ID format: " + idParam);
            }
        } else {
            System.out.println("No ID parameter found");
        }
    }

    public void loadCity() {
        if (cityId != null) {
            this.city = cityDAO.find(cityId);
            System.out.println("City loaded: " + (city != null));
            if (city != null) {
                System.out.println("City name: " + city.getName());
            }
        }
    }


    public City getCity() {
        return city;
    }

    public Integer getCityId() { // ИЗМЕНИТЕ возвращаемый тип
        return cityId;
    }

    public void setCityId(Integer cityId) { // ИЗМЕНИТЕ параметр
        this.cityId = cityId;
        loadCity();
    }

    public boolean isCityLoaded() {
        return city != null;
    }

    public String editCity() {
        if (city != null) {
            return "cityForm?faces-redirect=true&amp;id=" + city.getId();
        }
        return null;
    }

    public List<City> getOtherCitiesWithSameGovernor() {
        if (city == null || city.getGovernor() == null) return new ArrayList<>();

        return cityDAO.findByGovernorId(city.getGovernor().getId()).stream()
                .filter(c -> !c.getId().equals(city.getId()))
                .collect(Collectors.toList());
    }

    public boolean isHasOtherCitiesWithSameGovernor() {
        return !getOtherCitiesWithSameGovernor().isEmpty();
    }

    public int calculateGovernorAge() {
        if (city == null || city.getGovernor() == null ||
                city.getGovernor().getBirthday() == null) {
            return 0;
        }
        // Простой расчет возраста
        Calendar birth = Calendar.getInstance();
        birth.setTime(city.getGovernor().getBirthday());
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
    }
}