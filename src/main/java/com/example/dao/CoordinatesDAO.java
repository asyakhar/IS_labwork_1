package com.example.dao;

import com.example.model.Coordinates;
import jakarta.ejb.Stateless;

@Stateless
public class CoordinatesDAO extends GenericDAO<Coordinates> {
    public CoordinatesDAO() {
        super(Coordinates.class);
    }
}
