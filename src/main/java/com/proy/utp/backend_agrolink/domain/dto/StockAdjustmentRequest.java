package com.proy.utp.backend_agrolink.domain.dto;

public class StockAdjustmentRequest {
    private Double delta; // La cantidad a sumar o restar
    private String reason; // El motivo del ajuste

    // Getters y Setters
    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}