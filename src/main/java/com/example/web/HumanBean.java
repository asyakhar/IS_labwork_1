package com.example.web;

import com.example.dao.HumanDAO;
import com.example.model.Human;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("humanBean")
@RequestScoped
public class HumanBean implements Serializable {

    @Inject
    private HumanDAO humanDAO;

    private List<Human> allHumans;
    private Human newHuman = new Human();

    public List<Human> getAllHumans() {
        try {
            List<Human> humans = humanDAO.findAll();
            System.out.println("Загружено губернаторов: " + humans.size());
            for (Human human : humans) {
                System.out.println("   - " + human.getName() + " (ID: " + human.getId() + ")");
            }
            return humans;
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке губернаторов: " + e.getMessage());
            return List.of();
        }
    }
    public void refreshHumans() {
        allHumans = null;
    }
    public Human getNewHuman() {
        return newHuman;
    }

    public String createHuman() {
        try {
            humanDAO.create(newHuman);
            allHumans = null;
            newHuman = new Human();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}