package com.example.service;

import com.example.dao.CityDAO;
import com.example.model.City;
import com.example.model.Climate;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class CityService {

    @Inject
    private CityDAO cityDAO;

    public void createCity(City city) {
        cityDAO.create(city);
    }

    public City findCity(Integer id) {
        return cityDAO.find(id);
    }

    public City updateCity(City city) {
        return cityDAO.update(city);
    }

    public void deleteCity(City city) {
        cityDAO.delete(city);
    }

    public List<City> getAllCities() {
        return cityDAO.findAll();
    }

    // 🔹 спецоперации делегируем в DAO

    public void deleteOneByClimate(Climate climate) {
        cityDAO.deleteOneByClimate(climate);
    }

    public Long getSumMetersAboveSeaLevel() {
        return cityDAO.getSumMetersAboveSeaLevel();
    }

    public List<City> findByNamePrefix(String prefix) {
        return cityDAO.findByNamePrefix(prefix);
    }

    public Double getDistanceFromZeroToMinPopulation() {
        City city = cityDAO.findMinPopulationCity();
        double x = city.getCoordinates().getX();
        double y = city.getCoordinates().getY();
        double z = city.getMetersAboveSeaLevel() == null ? 0 : city.getMetersAboveSeaLevel();
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Double getDistanceMaxToMinPopulation() {
        City minCity = cityDAO.findMinPopulationCity();
        City maxCity = cityDAO.findMaxPopulationCity();

        double dx = maxCity.getCoordinates().getX() - minCity.getCoordinates().getX();
        double dy = maxCity.getCoordinates().getY() - minCity.getCoordinates().getY();
        double dz = (maxCity.getMetersAboveSeaLevel() == null ? 0 : maxCity.getMetersAboveSeaLevel())
                - (minCity.getMetersAboveSeaLevel() == null ? 0 : minCity.getMetersAboveSeaLevel());

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
