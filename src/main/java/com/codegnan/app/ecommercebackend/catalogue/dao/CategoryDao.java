package com.codegnan.app.ecommercebackend.catalogue.dao;

import java.util.List;

import com.codegnan.app.ecommercebackend.catalogue.dto.CategoryResponseDto;

public interface CategoryDao {
	List<CategoryResponseDto> findAllActive();
}
