package com.cavasini.CartMicroservice.model;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart implements Serializable {

    @Id
    private UUID userId;

    @Transient
    private List<CartItem> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(UUID userId){
        this.userId = userId;
        this.items = new ArrayList<>();
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", items=" + items +
                '}';
    }
}
