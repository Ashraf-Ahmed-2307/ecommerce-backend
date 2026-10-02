package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;

public interface ProductDao {
	Long save(ProductRequestDto productRequestDto);

	ProductResponseDto findById(long productId);

	List<ProductResponseDto> findAll();

	List<ProductResponseDto> findActiveByCategoryId(long categoryId);

	boolean updateName(long productId, String updatedName);

	boolean delete(long productId);
}
