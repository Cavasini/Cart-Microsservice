package com.cavasini.CartMicroservice.dto.request;

import java.util.UUID;

public record DeleteCartProductRequest(UUID userId, UUID productId) {
}
