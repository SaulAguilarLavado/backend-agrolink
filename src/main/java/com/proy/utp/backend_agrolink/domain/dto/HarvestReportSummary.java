package com.proy.utp.backend_agrolink.domain.dto;

import com.proy.utp.backend_agrolink.domain.Harvest;
import java.util.List;
import java.util.Map;

public class HarvestReportSummary {
    private long totalHarvestsCount;
    private Map<String, Double> totalQuantityByUnit;
    private List<Harvest> detailedHarvests;

    // Getters y Setters
    public long getTotalHarvestsCount() { return totalHarvestsCount; }
    public void setTotalHarvestsCount(long totalHarvestsCount) { this.totalHarvestsCount = totalHarvestsCount; }
    public Map<String, Double> getTotalQuantityByUnit() { return totalQuantityByUnit; }
    public void setTotalQuantityByUnit(Map<String, Double> totalQuantityByUnit) { this.totalQuantityByUnit = totalQuantityByUnit; }
    public List<Harvest> getDetailedHarvests() { return detailedHarvests; }
    public void setDetailedHarvests(List<Harvest> detailedHarvests) { this.detailedHarvests = detailedHarvests; }
}