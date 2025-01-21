package com.cavasini.CartMicroservice.dto.request;

import java.util.UUID;

public record CartUpdateRequest(UUID userId,
                                UUID productId,
                                String action,
                                Integer quantity) {
}
