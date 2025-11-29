package com.proy.utp.backend_agrolink.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleReport {
    private Long transactionId;
    private Long orderId;
    private LocalDateTime transactionDate;
    private BigDecimal totalAmount;
    private String buyerName;

    // Getters y Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
}