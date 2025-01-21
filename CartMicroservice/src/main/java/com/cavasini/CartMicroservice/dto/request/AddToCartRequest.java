package com.cavasini.CartMicroservice.dto.request;

import java.util.UUID;

public record AddToCartRequest(UUID userId,
                               UUID productId,
                               Integer quantity) {
}
