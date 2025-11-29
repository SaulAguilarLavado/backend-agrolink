package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.dto.CropReportSummary;
import com.proy.utp.backend_agrolink.domain.dto.HarvestReportSummary;
import com.proy.utp.backend_agrolink.domain.dto.SaleReport;
import com.proy.utp.backend_agrolink.domain.dto.SalesReportSummary;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.domain.repository.HarvestRepository;
import com.proy.utp.backend_agrolink.persistance.crud.TransaccionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final AuthenticatedUserService authenticatedUserService;
    private final TransaccionCrudRepository transaccionRepository;
    private final HarvestRepository harvestRepository;
    private final CropRepository cropRepository;

    public ReportService(AuthenticatedUserService authenticatedUserService,
                         TransaccionCrudRepository transaccionRepository,
                         HarvestRepository harvestRepository,
                         CropRepository cropRepository) {
        this.authenticatedUserService = authenticatedUserService;
        this.transaccionRepository = transaccionRepository;
        this.harvestRepository = harvestRepository;
        this.cropRepository = cropRepository;
    }

    /**
     * Genera un reporte de ventas para el agricultor autenticado.
     */
    public SalesReportSummary getSalesReportForFarmer() {
        User farmer = authenticatedUserService.getAuthenticatedUser();
        List<Transaccion> sales = transaccionRepository.findByVendedorId(farmer.getUserId());

        List<SaleReport> detailedSales = sales.stream().map(t -> {
            SaleReport dto = new SaleReport();
            dto.setTransactionId(t.getId());
            dto.setOrderId(t.getPedido().getId());
            dto.setTransactionDate(t.getFechaTransaccion());
            dto.setTotalAmount(t.getMontoTotal());
            dto.setBuyerName(t.getComprador().getNombre() + " " + t.getComprador().getApellido());
            return dto;
        }).collect(Collectors.toList());

        BigDecimal totalRevenue = detailedSales.stream()
                .map(SaleReport::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalesReportSummary summary = new SalesReportSummary();
        summary.setTotalSalesCount(detailedSales.size());
        summary.setTotalRevenue(totalRevenue);
        summary.setDetailedSales(detailedSales);

        return summary;
    }

    /**
     * Genera un reporte de cosechas para el agricultor autenticado.
     */
    public HarvestReportSummary getHarvestReportForFarmer() {
        User farmer = authenticatedUserService.getAuthenticatedUser();
        List<Harvest> harvests = harvestRepository.findByFarmerId(farmer.getUserId());

        Map<String, Double> totalQuantityByUnit = harvests.stream()
                .collect(Collectors.groupingBy(
                        Harvest::getUnitOfMeasure,
                        Collectors.summingDouble(Harvest::getQuantityHarvested)
                ));

        HarvestReportSummary summary = new HarvestReportSummary();
        summary.setTotalHarvestsCount(harvests.size());
        summary.setTotalQuantityByUnit(totalQuantityByUnit);
        summary.setDetailedHarvests(harvests);

        return summary;
    }

    /**
     * Genera un reporte de cultivos para el agricultor autenticado.
     */
    public CropReportSummary getCropReportForFarmer() {
        User farmer = authenticatedUserService.getAuthenticatedUser();
        List<Crop> crops = cropRepository.findByFarmerId(farmer.getUserId());

        Map<String, Long> statusCounts = crops.stream()
                .collect(Collectors.groupingBy(
                        Crop::getStatus,
                        Collectors.counting()
                ));

        CropReportSummary summary = new CropReportSummary();
        summary.setTotalCropsCount(crops.size());
        summary.setStatusCounts(statusCounts);
        summary.setDetailedCrops(crops);

        return summary;
    }
}