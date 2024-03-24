package com.nhat.controller;

import com.nhat.model.Product;
import com.nhat.response.CategoryProductsResponse;
import com.nhat.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> filterProductsHandle(@RequestParam String category,
             @RequestParam List<String> color, @RequestParam List<String> size,
             @RequestParam Integer minPrice, @RequestParam Integer maxPrice,
             @RequestParam Integer minDiscount, @RequestParam String sort,
             @RequestParam String stock, @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        Page<Product> res = productService.findAllProduct(category, color, size, minPrice, maxPrice, minDiscount, sort, stock, pageNumber, pageSize);
        System.out.println("complete products");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<Product> findProductById(@PathVariable Long productId) throws Exception {
        Product product = productService.findProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

//    @GetMapping("/products/search")
//    public ResponseEntity<List<Product>> searchProductsHandler(@PathVariable String q) {
//        List<Product> products = productService.searchProduct(q);
//        return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
//    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> findProductByCategory(@Param("category") String category) throws Exception {
        List<Product> products = productService.findProductByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.ACCEPTED);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<CategoryProductsResponse>> getLatestProductsByCategories(@RequestParam List<String> categories,
                                                                                        @RequestParam Integer numberOfProducts) {
        List<CategoryProductsResponse> latestProducts = productService.getLatestProductsByCategories(categories, numberOfProducts);
        return new ResponseEntity<>(latestProducts, HttpStatus.OK);
    }
}
