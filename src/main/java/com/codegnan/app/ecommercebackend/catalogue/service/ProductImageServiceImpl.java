package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codegnan.app.ecommercebackend.catalogue.dao.ProductDao;
import com.codegnan.app.ecommercebackend.catalogue.dao.ProductImageDao;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageResponseDto;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {
	private ProductDao productDao;
	private ProductImageDao productImageDao;

	@Autowired
	public ProductImageServiceImpl(ProductDao productDao, ProductImageDao productImageDao) {
		this.productDao = productDao;
		this.productImageDao = productImageDao;
	}

	@Override
	public void addImage(long productId, ProductImageRequestDto productImageRequestDto) {
		if (productImageRequestDto == null) {
			throw new IllegalArgumentException("Image details are required");
		}

		requireText("Image URL", productImageRequestDto.imageUrl(), 1024);
		requireText("Alt text", productImageRequestDto.altText(), 255);
		requireHttpOrRelativePath(productImageRequestDto.imageUrl());

		checkProductExists(productId);

		productImageDao.save(productId, productImageRequestDto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductImageResponseDto> getImages(long productId) {
		checkProductExists(productId);

		return productImageDao.findByProductId(productId);
	}

	private void checkProductExists(long productId) {
		if (productDao.findById(productId) == null) {
			throw new ResourceNotFoundException("Product not found: " + productId);
		}
	}

	private void requireHttpOrRelativePath(String imageUrl) {
		var value = imageUrl.trim().toLowerCase(Locale.ROOT);
		var isRelativePath = !value.contains(":");

		if (!isRelativePath && !value.startsWith("http://") && !value.startsWith("https://")) {
			throw new IllegalArgumentException("Image URL must be an http(s) URL or a relative path");
		}
	}

	private void requireText(String fieldName, String value, int maxLength) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		if (value.trim().length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters");
		}
	}
}
