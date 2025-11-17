package com.proy.utp.backend_agrolink.domain.dto;

import java.math.BigDecimal;

public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal pricePerUnit;
    private Double availableStock;

    //<editor-fold desc="Getters y Setters">
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public Double getAvailableStock() { return availableStock; }
    public void setAvailableStock(Double availableStock) { this.availableStock = availableStock; }
    //</editor-fold>
}