package com.codegnan.app.ecommercebackend.catalogue.dto;

import java.util.List;

public record ProductRequestDto(String name, String brand, String description, String status, List<Long> categoryIds) {
}
