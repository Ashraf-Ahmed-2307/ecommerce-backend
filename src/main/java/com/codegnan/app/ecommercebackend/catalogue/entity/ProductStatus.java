package com.codegnan.app.ecommercebackend.catalogue.entity;

import java.util.Locale;

public enum ProductStatus {
	DRAFT, ACTIVE, INACTIVE, DISCONTINUED;

	public static ProductStatus from(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Product status is required");
		}

		try {
			return ProductStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException("Invalid product status: " + value);
		}
	}
}
