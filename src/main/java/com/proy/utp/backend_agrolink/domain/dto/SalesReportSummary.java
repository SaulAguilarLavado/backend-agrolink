package com.proy.utp.backend_agrolink.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public class SalesReportSummary {
    private long totalSalesCount;
    private BigDecimal totalRevenue;
    private List<SaleReport> detailedSales;

    // Getters y Setters
    public long getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(long totalSalesCount) { this.totalSalesCount = totalSalesCount; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public List<SaleReport> getDetailedSales() { return detailedSales; }
    public void setDetailedSales(List<SaleReport> detailedSales) { this.detailedSales = detailedSales; }
}