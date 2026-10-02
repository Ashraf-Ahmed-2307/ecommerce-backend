package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codegnan.app.ecommercebackend.catalogue.dao.ProductDao;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
	private ProductDao productDao;

	@Autowired
	public ProductServiceImpl(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Override
	public void addProduct(ProductRequestDto productRequestDto) {
		if (productRequestDto == null) {
			throw new IllegalArgumentException("Product details are required");
		}

		requireText("Name", productRequestDto.name(), 160);
		requireText("Description", productRequestDto.description(), 5000);

		if (productRequestDto.brand() != null && productRequestDto.brand().trim().length() > 100) {
			throw new IllegalArgumentException("Brand must be at most 100 characters");
		}

		productDao.save(productRequestDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponseDto searchProductById(long productId) {
		var product = productDao.findById(productId);

		if (product == null) {
			throw new ResourceNotFoundException("Product not found: " + productId);
		}

		return product;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponseDto> getAllProducts() {
		return productDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponseDto> getProductsByCategory(long categoryId) {
		return productDao.findActiveByCategoryId(categoryId);
	}

	@Override
	public void renameProduct(long productId, String updatedName) {
		requireText("Name", updatedName, 160);

		if (!productDao.updateName(productId, updatedName)) {
			throw new ResourceNotFoundException("Product not found: " + productId);
		}
	}

	@Override
	public void removeProduct(long productId) {
		if (!productDao.delete(productId)) {
			throw new ResourceNotFoundException("Product not found: " + productId);
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
