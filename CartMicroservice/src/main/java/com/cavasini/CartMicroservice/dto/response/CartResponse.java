package com.cavasini.CartMicroservice.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(UUID cartId, List<CartItemDTO> items, BigDecimal totalPrice) {
}
