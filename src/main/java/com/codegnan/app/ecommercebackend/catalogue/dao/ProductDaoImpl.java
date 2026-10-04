package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.entity.Category;
import com.codegnan.app.ecommercebackend.catalogue.entity.Product;
import com.codegnan.app.ecommercebackend.catalogue.entity.ProductImage;
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
		ProductResponseDto productResponseDto = null;

		var product = entityManager.find(Product.class, productId);

		if (product != null) {
			List<Long> productIds = new ArrayList<>();
			productIds.add(product.getId());

			var covers = findCoverImages(productIds);

			productResponseDto = toDto(product, covers.get(product.getId()));
		}

		return productResponseDto;
	}

	@Override
	public List<ProductResponseDto> findAll() {
		var jpql = "SELECT p FROM Product p ORDER BY p.id";

		List<Product> productsList = entityManager.createQuery(jpql, Product.class).getResultList();

		return toDtos(productsList);
	}

	@Override
	public List<ProductResponseDto> findActiveByCategoryId(long categoryId) {
		var jpql = "SELECT p FROM Product p JOIN p.categories c "
				+ "WHERE c.id = :categoryId AND c.active = true AND p.status = :status "
				+ "ORDER BY p.createdAt DESC, p.id DESC";

		List<Product> productsList = entityManager.createQuery(jpql, Product.class)
				.setParameter("categoryId", categoryId)
				.setParameter("status", ProductStatus.ACTIVE)
				.getResultList();

		return toDtos(productsList);
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
		//entityManager.flush();

		return true;
	}

	private Map<Long, ProductImage> findCoverImages(List<Long> productIds) {
		Map<Long, ProductImage> covers = new HashMap<>();

		if (productIds.isEmpty()) {
			return covers;
		}

		var jpql = "SELECT i FROM ProductImage i WHERE i.productId IN :productIds "
				+ "AND i.displayOrder = (SELECT MIN(m.displayOrder) FROM ProductImage m WHERE m.productId = i.productId)";

		List<ProductImage> imagesList = entityManager.createQuery(jpql, ProductImage.class)
				.setParameter("productIds", productIds)
				.getResultList();

		for (var image : imagesList) {
			covers.put(image.getProductId(), image);
		}

		return covers;
	}

	private List<ProductResponseDto> toDtos(List<Product> productsList) {
		List<Long> productIds = new ArrayList<>();

		for (var product : productsList) {
			productIds.add(product.getId());
		}

		var covers = findCoverImages(productIds);

		List<ProductResponseDto> productDtosList = new ArrayList<>();

		for (var product : productsList) {
			productDtosList.add(toDto(product, covers.get(product.getId())));
		}

		return productDtosList;
	}

	private ProductResponseDto toDto(Product product, ProductImage cover) {
		return new ProductResponseDto(
				product.getId(),
				product.getName(),
				product.getBrand(),
				product.getDescription(),
				product.getStatus().name(),
				product.getCreatedAt(),
				product.getUpdatedAt(),
				cover == null ? null : cover.getImageUrl(),
				cover == null ? null : cover.getAltText());
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
