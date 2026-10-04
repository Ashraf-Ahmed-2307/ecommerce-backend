package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.entity.ProductImage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class ProductImageDaoImpl implements ProductImageDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long save(long productId, ProductImageRequestDto productImageRequestDto) {
		var jpql = "SELECT COALESCE(MAX(i.displayOrder), -1) + 1 FROM ProductImage i WHERE i.productId = :productId";

		int nextOrder = entityManager.createQuery(jpql, Number.class)
				.setParameter("productId", productId)
				.getSingleResult()
				.intValue();

		var image = new ProductImage();
		image.setProductId(productId);
		image.setImageUrl(productImageRequestDto.imageUrl().trim());
		image.setAltText(productImageRequestDto.altText().trim());
		image.setDisplayOrder(nextOrder);
		image.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

		entityManager.persist(image);

		return image.getId();
	}

	@Override
	public List<ProductImageResponseDto> findByProductId(long productId) {
		List<ProductImageResponseDto> imageDtosList = new ArrayList<>();

		var jpql = "SELECT i FROM ProductImage i WHERE i.productId = :productId ORDER BY i.displayOrder";

		List<ProductImage> imagesList = entityManager.createQuery(jpql, ProductImage.class)
				.setParameter("productId", productId)
				.getResultList();

		for (var image : imagesList) {
			var productImageResponseDto = new ProductImageResponseDto(
					image.getId(),
					image.getProductId(),
					image.getImageUrl(),
					image.getAltText(),
					image.getDisplayOrder());

			imageDtosList.add(productImageResponseDto);
		}

		return imageDtosList;
	}
}
