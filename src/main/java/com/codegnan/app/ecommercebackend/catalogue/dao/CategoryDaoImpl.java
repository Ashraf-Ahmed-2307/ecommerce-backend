package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codegnan.app.ecommercebackend.catalogue.dto.CategoryResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.entity.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CategoryDaoImpl implements CategoryDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<CategoryResponseDto> findAllActive() {
		List<CategoryResponseDto> categoryDtosList = new ArrayList<>();

		var jpql = "SELECT c FROM Category c WHERE c.active = true ORDER BY c.name";

		List<Category> categoriesList = entityManager.createQuery(jpql, Category.class).getResultList();

		for (var category : categoriesList) {
			var categoryResponseDto = new CategoryResponseDto(
					category.getId(),
					category.getName(),
					category.getSlug(),
					category.getDescription());

			categoryDtosList.add(categoryResponseDto);
		}

		return categoryDtosList;
	}
}
