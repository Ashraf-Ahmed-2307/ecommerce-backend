package com.codegnan.app.ecommercebackend.resource;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codegnan.app.ecommercebackend.catalogue.dto.CategoryResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductImageResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductRequestDto;
import com.codegnan.app.ecommercebackend.catalogue.dto.ProductResponseDto;
import com.codegnan.app.ecommercebackend.catalogue.service.CategoryService;
import com.codegnan.app.ecommercebackend.catalogue.service.ProductImageService;
import com.codegnan.app.ecommercebackend.catalogue.service.ProductService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/rest/api/v1/catalogues")
public class CatalogueResource {
	private ProductService productService;
	private CategoryService categoryService;
	private ProductImageService productImageService;

	public CatalogueResource(ProductService productService, CategoryService categoryService,
			ProductImageService productImageService) {
		this.productService = productService;
		this.categoryService = categoryService;
		this.productImageService = productImageService;
	}

	@PostMapping
	public String addProductOperation(@ModelAttribute ProductRequestDto productRequestDto) {
		productService.addProduct(productRequestDto);

		return "success";
	}

	@GetMapping
	public ProductResponseDto searchProductOperation(@RequestParam long productId) {
		return productService.searchProductById(productId);
	}

	@GetMapping("/viewall")
	public List<ProductResponseDto> getAllProductsOperation() {
		return productService.getAllProducts();
	}

	@GetMapping("/categories")
	public List<CategoryResponseDto> getCategoriesOperation() {
		return categoryService.getActiveCategories();
	}

	@GetMapping("/categories/{categoryId}/products")
	public List<ProductResponseDto> getProductsByCategoryOperation(@PathVariable long categoryId) {
		return productService.getProductsByCategory(categoryId);
	}

	@PostMapping("/{productId}/images")
	public String addProductImageOperation(@PathVariable long productId,
			@ModelAttribute ProductImageRequestDto productImageRequestDto) {
		productImageService.addImage(productId, productImageRequestDto);

		return "success";
	}

	@GetMapping("/{productId}/images")
	public List<ProductImageResponseDto> getProductImagesOperation(@PathVariable long productId) {
		return productImageService.getImages(productId);
	}

	@PutMapping
	public String updateProductNameOperation(@RequestParam long productId, @RequestParam String updatedName) {
		productService.renameProduct(productId, updatedName);

		return "success";
	}

	@DeleteMapping
	public String removeProductOperation(@RequestParam long productId) {
		productService.removeProduct(productId);

		return "success";
	}
}
