package com.proy.utp.backend_agrolink.domain.dto;

import java.util.List;

public class OrderRequest {

    private List<OrderItem> items;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
