package com.codegnan.app.ecommercebackend.catalogue.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;
	@Column(name = "product_id", nullable = false)
	private Long productId;
	@Column(name = "image_url", nullable = false, length = 1024)
	private String imageUrl;
	@Column(name = "alt_text", nullable = false)
	private String altText;
	@Column(name = "display_order", nullable = false)
	private int displayOrder;
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public ProductImage() {
	}

	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return productId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getAltText() {
		return altText;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "ProductImage [id=" + id + ", productId=" + productId + ", imageUrl=" + imageUrl
				+ ", displayOrder=" + displayOrder + "]";
	}
}
