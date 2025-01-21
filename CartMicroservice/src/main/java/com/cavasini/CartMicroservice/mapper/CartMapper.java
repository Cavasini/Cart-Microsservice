package com.cavasini.CartMicroservice.mapper;

import com.cavasini.CartMicroservice.dto.event.CartUpdateEvent;
import com.cavasini.CartMicroservice.dto.request.AddToCartRequest;
import com.cavasini.CartMicroservice.dto.response.CartResponse;
import com.cavasini.CartMicroservice.model.CartItem;
import com.cavasini.CartMicroservice.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartMapper {

    public CartItem toCartItem(AddToCartRequest request, Product product) {
        return new CartItem(
                request.productId(),
                product.getName(),
                request.quantity(),
                product.getPrice(),
                request.userId()
        );
    }

    public CartUpdateEvent toAddToCartEvent(UUID id, AddToCartRequest request, Product product) {
        return new CartUpdateEvent(
                id,
                request.productId(),
                product.getName(),
                request.quantity(),
                product.getPrice(),
                request.userId()
        );
    }

    public CartUpdateEvent toUpdateCartEvent(UUID id, UUID userId, Integer quantity) {
        return new CartUpdateEvent(
                id,
                null,
                null,
                quantity,
                null,
                userId
        );
    }

    public CartUpdateEvent toDeleteCartEvent(UUID id, UUID userId) {
        return new CartUpdateEvent(
                id,
                null,
                null,
                null,
                null,
                userId
        );
    }

    public CartResponse toEmptyCartResponse(UUID userId) {
        return new CartResponse(
                userId,
                List.of(),
                BigDecimal.ZERO
        );
    }
}
