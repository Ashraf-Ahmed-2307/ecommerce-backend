package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.entity.Category;
import com.codegnan.app.ecommercebackend.catalogue.entity.Product;
import com.codegnan.app.ecommercebackend.catalogue.entity.ProductStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class ProductDaoImpl implements ProductDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long save(ProductRequestDto productRequestDto) {
		var now = LocalDateTime.now(ZoneOffset.UTC);

		var product = new Product();
		product.setName(productRequestDto.name().trim());
		product.setBrand(blankToNull(productRequestDto.brand()));
		product.setDescription(productRequestDto.description().trim());
		product.setStatus(ProductStatus.from(productRequestDto.status()));
		product.setCreatedAt(now);
		product.setUpdatedAt(now);

		if (productRequestDto.categoryIds() != null) {
			for (var categoryId : new LinkedHashSet<>(productRequestDto.categoryIds())) {
				var category = entityManager.find(Category.class, categoryId);

				if (category == null) {
					throw new IllegalArgumentException("Category not found: " + categoryId);
				}

				product.getCategories().add(category);
			}
		}

		entityManager.persist(product);

		return product.getId();
	}

	@Override
	public ProductResponseDto findById(long productId) {
		var product = entityManager.find(Product.class, productId);

		return product == null ? null : toDto(product);
	}

	@Override
	public List<ProductResponseDto> findAll() {
		var jpql = "SELECT p FROM Product p ORDER BY p.id";

		return entityManager.createQuery(jpql, Product.class)
				.getResultList()
				.stream()
				.map(this::toDto)
				.toList();
	}

	@Override
	public List<ProductResponseDto> findActiveByCategoryId(long categoryId) {
		var jpql = "SELECT p FROM Product p JOIN p.categories c "
				+ "WHERE c.id = :categoryId AND c.active = true AND p.status = :status "
				+ "ORDER BY p.createdAt DESC, p.id DESC";

		return entityManager.createQuery(jpql, Product.class)
				.setParameter("categoryId", categoryId)
				.setParameter("status", ProductStatus.ACTIVE)
				.getResultList()
				.stream()
				.map(this::toDto)
				.toList();
	}

	@Override
	public boolean updateName(long productId, String updatedName) {
		var product = entityManager.find(Product.class, productId);

		if (product == null) {
			return false;
		}

		product.setName(updatedName.trim());
		product.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		return true;
	}

	@Override
	public boolean delete(long productId) {
		var product = entityManager.find(Product.class, productId);

		if (product == null) {
			return false;
		}

		entityManager.remove(product);
		entityManager.flush();

		return true;
	}

	private ProductResponseDto toDto(Product product) {
		return new ProductResponseDto(
				product.getId(),
				product.getName(),
				product.getBrand(),
				product.getDescription(),
				product.getStatus().name(),
				product.getCreatedAt(),
				product.getUpdatedAt());
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
