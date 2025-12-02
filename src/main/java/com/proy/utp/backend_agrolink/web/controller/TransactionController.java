package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.dto.TransactionDto;
import com.proy.utp.backend_agrolink.domain.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacciones")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<TransactionDto>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/mis-ventas")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<List<TransactionDto>> getMySales() {
        return ResponseEntity.ok(transactionService.getMySales());
    }

    @GetMapping("/mis-compras")
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<List<TransactionDto>> getMyPurchases() {
        return ResponseEntity.ok(transactionService.getMyPurchases());
    }
}
