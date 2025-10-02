package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "coordinates")
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float x;

    @NotNull
    @Column(nullable = false)
    private Double y; // > -40

    public Coordinates() {}

    public Coordinates(float x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() { return id; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
}
