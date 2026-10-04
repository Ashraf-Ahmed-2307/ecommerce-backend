package com.codegnan.app.ecommercebackend.catalogue.dto;

import java.time.LocalDateTime;

public record ProductResponseDto(Long id, String name, String brand, String description, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String coverImageUrl, String coverImageAlt) {
}
