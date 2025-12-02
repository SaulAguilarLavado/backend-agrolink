package com.proy.utp.backend_agrolink.domain.dto;

import com.proy.utp.backend_agrolink.domain.Crop;
import java.util.List;
import java.util.Map;

public class CropReportSummary {
    private long totalCropsCount;
    private Map<String, Long> statusCounts;
    private List<Crop> detailedCrops;

    // Getters y Setters
    public long getTotalCropsCount() { return totalCropsCount; }
    public void setTotalCropsCount(long totalCropsCount) { this.totalCropsCount = totalCropsCount; }
    public Map<String, Long> getStatusCounts() { return statusCounts; }
    public void setStatusCounts(Map<String, Long> statusCounts) { this.statusCounts = statusCounts; }
    public List<Crop> getDetailedCrops() { return detailedCrops; }
    public void setDetailedCrops(List<Crop> detailedCrops) { this.detailedCrops = detailedCrops; }
}