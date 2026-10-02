package com.codegnan.app.ecommercebackend.catalogue.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codegnan.app.ecommercebackend.catalogue.dao.CategoryDao;
import com.codegnan.app.ecommercebackend.catalogue.dto.CategoryResponseDto;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
	private CategoryDao categoryDao;

	@Autowired
	public CategoryServiceImpl(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public List<CategoryResponseDto> getActiveCategories() {
		return categoryDao.findAllActive();
	}
}
