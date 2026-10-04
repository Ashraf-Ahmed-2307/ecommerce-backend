package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageResponseDto;

public interface ProductImageService {
	void addImage(long productId, ProductImageRequestDto productImageRequestDto);

	List<ProductImageResponseDto> getImages(long productId);
}
