package com.example.web;

import com.example.dao.CityDAO;
import com.example.dao.HumanDAO;
import com.example.model.City;
import com.example.model.Coordinates;
import com.example.model.Human;
import com.example.model.Climate;
import com.example.model.StandardOfLiving;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("cityBean")
@SessionScoped
public class CityBean implements Serializable {

    @Inject
    private CityDAO cityDAO;

    @Inject
    private HumanDAO humanDAO;

    private List<City> cities;
    private City selectedCity;
    private Long selectedGovernorId;
    private Human newGovernor = new Human();
    private boolean createNewGovernor = true;
    private Integer editCityId;

    private Integer searchCityId;
    private City searchResult;
    private String searchError;

    public List<City> getCities() {
        try {
            if (cities == null) {
                cities = cityDAO.findAll();
            }
            return cities;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void loadCityForEdit() {
        if (editCityId != null) {
            selectedCity = cityDAO.find(editCityId);
            if (selectedCity.getCoordinates() == null) {
                selectedCity.setCoordinates(new Coordinates());
            }
            if (selectedCity.getGovernor() == null) {
                selectedCity.setGovernor(new Human());
            }
            createNewGovernor = false;
            selectedGovernorId = selectedCity.getGovernor().getId();
            newGovernor = new Human();
        }
    }

    public Integer getEditCityId() {
        return editCityId;
    }

    public void setEditCityId(Integer editCityId) {
        this.editCityId = editCityId;
    }

    public City getSelectedCity() {
        if (selectedCity == null) {
            selectedCity = new City();
            selectedCity.setCoordinates(new Coordinates());
            selectedCity.setGovernor(new Human());
        }
        return selectedCity;
    }

    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;
    }


    public void deleteCityWithRelations(City city) {
        try {
            if (city != null) {
                cityDAO.deleteCityWithRelations(city.getId());
                cities = null; // Сбросить кэш

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Успех",
                                "Город '" + city.getName() + "' и связанные объекты удалены"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка",
                            "Не удалось удалить город: " + e.getMessage()));
        }
    }

    public String saveCity() {
        try {
            System.out.println("=== НАЧАЛО СОХРАНЕНИЯ ГОРОДА ===");

            // 🔥 ВАЖНО: ВСЯ логика в одной транзакции

            // Обработка губернатора
            if (createNewGovernor) {
                if (newGovernor.getName() == null || newGovernor.getName().trim().isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Имя губернатора обязательно"));
                    return null;
                }
                // 🔥 НЕ сохраняем губернатора здесь - только устанавливаем связь
                selectedCity.setGovernor(newGovernor);
                System.out.println("✅ Новый губернатор подготовлен: " + newGovernor.getName());

            } else {
                // Использовать существующего губернатора
                if (selectedGovernorId != null) {
                    Human existingGovernor = humanDAO.find(selectedGovernorId);
                    if (existingGovernor == null) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Выбранный губернатор не найден"));
                        return null;
                    }
                    selectedCity.setGovernor(existingGovernor);
                    System.out.println("✅ Использован существующий губернатор ID: " + existingGovernor.getId() + ", Имя: " + existingGovernor.getName());
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Выберите губернатора"));
                    return null;
                }
            }

            // Валидация остальных полей
            if (selectedCity.getName() == null || selectedCity.getName().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Название города обязательно"));
                return null;
            }

            if (selectedCity.getArea() == null || selectedCity.getArea() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Площадь должна быть больше 0"));
                return null;
            }

            if (selectedCity.getPopulation() == null || selectedCity.getPopulation() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Население должно быть больше 0"));
                return null;
            }

            if (selectedCity.getStandardOfLiving() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Уровень жизни обязателен"));
                return null;
            }

            if (selectedCity.getCoordinates() == null || selectedCity.getCoordinates().getY() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Координаты обязательны"));
                return null;
            }

            // Отладочная информация
            System.out.println("Сохранение города: " + selectedCity.getName());
            System.out.println("Губернатор ID: " + (selectedCity.getGovernor() != null ? selectedCity.getGovernor().getId() : "null"));
            System.out.println("Координаты: (" + selectedCity.getCoordinates().getX() + ", " + selectedCity.getCoordinates().getY() + ")");

            // 🔥 СОХРАНЕНИЕ ВСЕГО В ОДНОЙ ТРАНЗАКЦИИ
            if (selectedCity.getId() == null) {
                // 🔥 ИСПОЛЬЗУЕМ СПЕЦИАЛЬНЫЙ МЕТОД ДЛЯ СОЗДАНИЯ ГОРОДА
                cityDAO.createCityWithRelations(selectedCity);
                System.out.println("✅ Город и губернатор сохранены в одной транзакции");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Успех", "Город успешно создан"));
            } else {
                cityDAO.update(selectedCity);
                System.out.println("✅ Город обновлен ID: " + selectedCity.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Успех", "Город успешно обновлен"));
            }

            resetForm();
            return "cityTable?faces-redirect=true";

        } catch (Exception e) {
            System.err.println("❌ Ошибка при сохранении города: " + e.getMessage());
            e.printStackTrace();
            // 🔥 Губернатор НЕ сохранился, т.к. транзакция откатилась
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Не удалось сохранить город: " + e.getMessage()));
            return null;
        }
    }

    private void resetForm() {
        selectedCity = null;
        selectedGovernorId = null;
        newGovernor = new Human();
        createNewGovernor = true;
        cities = null; // Сбросить кэш
    }

    public String newCity() {
        selectedCity = new City();
        selectedCity.setCoordinates(new Coordinates());
        selectedCity.setGovernor(new Human());
        createNewGovernor = true;
        newGovernor = new Human();
        selectedGovernorId = null;
        return "cityForm?faces-redirect=true";
    }

    public String editCity(City city) {
        this.selectedCity = city;
        return "cityForm?faces-redirect=true";
    }

    public String searchById() {
        try {
            searchError = null;
            searchResult = null;

            if (searchCityId == null || searchCityId <= 0) {
                searchError = "Введите корректный ID города";
                return null;
            }

            searchResult = cityDAO.find(searchCityId);
            if (searchResult == null) {
                searchError = "Город с ID " + searchCityId + " не найден";
            }

            return null;
        } catch (Exception e) {
            searchError = "Ошибка при поиске: " + e.getMessage();
            return null;
        }
    }

    // Геттеры и сеттеры
    public Integer getSearchCityId() { return searchCityId; }
    public void setSearchCityId(Integer searchCityId) { this.searchCityId = searchCityId; }
    public City getSearchResult() { return searchResult; }
    public String getSearchError() { return searchError; }
    public Long getSelectedGovernorId() { return selectedGovernorId; }
    public void setSelectedGovernorId(Long selectedGovernorId) { this.selectedGovernorId = selectedGovernorId; }
    public Human getNewGovernor() { return newGovernor; }
    public boolean isCreateNewGovernor() { return createNewGovernor; }
    public void setCreateNewGovernor(boolean createNewGovernor) { this.createNewGovernor = createNewGovernor; }
}