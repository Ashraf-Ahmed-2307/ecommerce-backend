package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageResponseDto;

public interface ProductImageDao {
	Long save(long productId, ProductImageRequestDto productImageRequestDto);

	List<ProductImageResponseDto> findByProductId(long productId);
}
