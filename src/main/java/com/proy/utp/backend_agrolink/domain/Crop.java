package com.proy.utp.backend_agrolink.domain;

import java.time.LocalDate;

public class Crop {
    private Long id;
    private String name;
    private String description;
    private LocalDate plantingDate;
    private Double cultivatedArea;
    private User farmer; // <-- ¡CORREGIDO! Ahora es el objeto User completo

    // --- GETTERS Y SETTERS CORREGIDOS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public Double getCultivatedArea() {
        return cultivatedArea;
    }

    public void setCultivatedArea(Double cultivatedArea) {
        this.cultivatedArea = cultivatedArea;
    }

    // Corregido para trabajar con el objeto User
    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }
}