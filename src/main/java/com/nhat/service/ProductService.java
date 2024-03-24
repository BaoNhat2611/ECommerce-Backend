package com.nhat.service;

import com.nhat.exception.ProductException;
import com.nhat.model.Product;
import com.nhat.request.CreateProductRequest;
import com.nhat.response.CategoryProductsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductRequest productRequest);
    String deleteProduct(Long productId) throws ProductException;
    Product updateProduct(Long productId, Product product) throws ProductException;
    Product findProductById(Long productId) throws ProductException;
    List<Product> findProductByCategory(String category);
    Page<Product> findAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice,
                                 Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);

    List<Product> findAllProducts();
    List<CategoryProductsResponse> getLatestProductsByCategories(List<String> categories, Integer numberOfProducts);
}
