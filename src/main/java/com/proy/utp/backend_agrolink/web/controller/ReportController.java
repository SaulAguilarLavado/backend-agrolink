package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.dto.CropReportSummary;
import com.proy.utp.backend_agrolink.domain.dto.HarvestReportSummary;
import com.proy.utp.backend_agrolink.domain.dto.SalesReportSummary;
import com.proy.utp.backend_agrolink.domain.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<SalesReportSummary> getSalesReport() {
        return ResponseEntity.ok(reportService.getSalesReportForFarmer());
    }

    @GetMapping("/harvests")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<HarvestReportSummary> getHarvestReport() {
        return ResponseEntity.ok(reportService.getHarvestReportForFarmer());
    }

    @GetMapping("/crops")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<CropReportSummary> getCropReport() {
        return ResponseEntity.ok(reportService.getCropReportForFarmer());
    }
}