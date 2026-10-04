package com.codegnan.app.ecommercebackend.catalogue.dto;

public record ProductImageResponseDto(Long id, Long productId, String imageUrl, String altText, int displayOrder) {
}
