package com.proy.utp.backend_agrolink.domain.dto;

import java.time.LocalDate;

public class CropDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate plantingDate;
    private Double cultivatedArea;
    private Long farmerId;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public Double getCultivatedArea() {
        return cultivatedArea;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public void setCultivatedArea(Double cultivatedArea) {
        this.cultivatedArea = cultivatedArea;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }
}
