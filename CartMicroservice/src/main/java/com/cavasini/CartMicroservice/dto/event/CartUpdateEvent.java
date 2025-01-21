package com.cavasini.CartMicroservice.dto.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record CartUpdateEvent(UUID id,
                              UUID productId,
                              String name,
                              Integer quantity,
                              BigDecimal price,
                              UUID userId) implements Serializable {
}
