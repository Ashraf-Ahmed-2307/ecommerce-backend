package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.CategoryResponseDto;

public interface CategoryService {
	List<CategoryResponseDto> getActiveCategories();
}
