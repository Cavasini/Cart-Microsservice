package com.cavasini.CartMicroservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDTO(UUID id,UUID productId, String productName, Integer quantity, BigDecimal price) {
}
