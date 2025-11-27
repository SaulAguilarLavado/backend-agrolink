package com.proy.utp.backend_agrolink.domain;

import java.time.LocalDate;

public class Harvest {
    private Long id;
    private Long cropId; // ID del cultivo del que proviene
    private LocalDate harvestDate;
    private Double quantityHarvested;
    private String unitOfMeasure;
    private String qualityNotes; // Para registrar la calidad, etc.

    //<editor-fold desc="Getters y Setters">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }
    public LocalDate getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }
    public Double getQuantityHarvested() { return quantityHarvested; }
    public void setQuantityHarvested(Double quantityHarvested) { this.quantityHarvested = quantityHarvested; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public String getQualityNotes() { return qualityNotes; }
    public void setQualityNotes(String qualityNotes) { this.qualityNotes = qualityNotes; }
    //</editor-fold>
}