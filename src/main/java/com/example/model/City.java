package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false, updatable = false)
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double area;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer population;

    private LocalDateTime establishmentDate;

    private Boolean capital;

    private Long metersAboveSeaLevel;

    @Min(-13)
    @Max(15)
    private long timezone;

    @Enumerated(EnumType.STRING)
    private Climate climate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandardOfLiving standardOfLiving;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "governor_id", nullable = false)
    private Human governor;

    public City() {}

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public String getCreationDateString() {
        if (creationDate == null) return "";
        return creationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getEstablishmentDateString() {
        if (establishmentDate == null) return "";
        return establishmentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getCapitalString() {
        if (capital == null) return "Нет";
        return capital ? "Да" : "Нет";
    }

    public String getTimezoneString() {
        return "UTC" + (timezone >= 0 ? "+" : "") + timezone;
    }

    public String getClimateString() {
        return climate != null ? climate.toString() : "Не указан";
    }

    public String getStandardOfLivingString() {
        return standardOfLiving != null ? standardOfLiving.toString() : "Не указан";
    }

    public ZonedDateTime getCreationDate() { return creationDate; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }

    public LocalDateTime getEstablishmentDate() { return establishmentDate; }
    public void setEstablishmentDate(LocalDateTime establishmentDate) { this.establishmentDate = establishmentDate; }

    public Boolean getCapital() { return capital; }
    public void setCapital(Boolean capital) { this.capital = capital; }

    public Long getMetersAboveSeaLevel() { return metersAboveSeaLevel; }
    public void setMetersAboveSeaLevel(Long metersAboveSeaLevel) { this.metersAboveSeaLevel = metersAboveSeaLevel; }

    public long getTimezone() { return timezone; }
    public void setTimezone(long timezone) { this.timezone = timezone; }

    public Climate getClimate() { return climate; }
    public void setClimate(Climate climate) { this.climate = climate; }

    public StandardOfLiving getStandardOfLiving() { return standardOfLiving; }
    public void setStandardOfLiving(StandardOfLiving standardOfLiving) { this.standardOfLiving = standardOfLiving; }

    public Human getGovernor() { return governor; }
    public void setGovernor(Human governor) { this.governor = governor; }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}