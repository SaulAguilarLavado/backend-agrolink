package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.dto.AdminUserDto;
import com.proy.utp.backend_agrolink.domain.service.AdminService;
import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) { this.adminService = adminService; }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/sales-report")
    public ResponseEntity<List<Transaccion>> getSalesReport(@RequestParam String period) {
        return ResponseEntity.ok(adminService.getSalesReportByPeriod(period));
    }
}