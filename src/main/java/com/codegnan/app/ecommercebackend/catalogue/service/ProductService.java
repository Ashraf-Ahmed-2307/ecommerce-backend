package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;

public interface ProductService {
	void addProduct(ProductRequestDto productRequestDto);

	ProductResponseDto searchProductById(long productId);

	List<ProductResponseDto> getAllProducts();

	List<ProductResponseDto> getProductsByCategory(long categoryId);

	void renameProduct(long productId, String updatedName);

	void removeProduct(long productId);
}
