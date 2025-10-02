package com.example.web;

import com.example.model.City;
import com.example.model.Climate;
import com.example.service.CityService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("specialBean")
@SessionScoped
public class SpecialOperationsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private CityService cityService;

    private Climate climate;
    private String namePrefix;
    private Long sumMeters;
    private Double distanceZeroMin;
    private Double distanceMaxMin;
    private List<City> foundCities;

    public void deleteByClimate() {
        cityService.deleteOneByClimate(climate);
    }

    public void calculateSum() {
        sumMeters = cityService.getSumMetersAboveSeaLevel();
    }

    public void searchByPrefix() {
        foundCities = cityService.findByNamePrefix(namePrefix);
    }

    public void calcZeroMin() {
        distanceZeroMin = cityService.getDistanceFromZeroToMinPopulation();
    }

    public void calcMaxMin() {
        distanceMaxMin = cityService.getDistanceMaxToMinPopulation();
    }

    // Getters / Setters
    public Climate getClimate() { return climate; }
    public void setClimate(Climate climate) { this.climate = climate; }
    public String getNamePrefix() { return namePrefix; }
    public void setNamePrefix(String namePrefix) { this.namePrefix = namePrefix; }
    public Long getSumMeters() { return sumMeters; }
    public Double getDistanceZeroMin() { return distanceZeroMin; }
    public Double getDistanceMaxMin() { return distanceMaxMin; }
    public List<City> getFoundCities() { return foundCities; }
}
