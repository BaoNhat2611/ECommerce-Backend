package com.nhat.service;

import com.nhat.exception.ProductException;
import com.nhat.model.Category;
import com.nhat.model.Product;
import com.nhat.repository.CategoryRepository;
import com.nhat.repository.ProductRepository;
import com.nhat.request.CreateProductRequest;
import com.nhat.response.CategoryProductsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{
    private UserService userService;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public ProductServiceImpl(UserService userService, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest productRequest) {

        Category topLevel = categoryRepository.findByName(productRequest.getTopLavelCategory());
        if (topLevel==null) {
            Category topLevelCategory = new Category();
            topLevelCategory.setName(productRequest.getTopLavelCategory());
            topLevelCategory.setLevel(1);

            topLevel = categoryRepository.save(topLevelCategory);
        }
        Category secondLevel = categoryRepository.findByNameAndParant(productRequest.getSecondLavelCategory(), topLevel.getName());
        if (secondLevel==null) {
            Category secondLevelCategory = new Category();
            secondLevelCategory.setName(productRequest.getSecondLavelCategory());
            secondLevelCategory.setParentCategory(topLevel);
            secondLevelCategory.setLevel(2);

            secondLevel = categoryRepository.save(secondLevelCategory);
        }
        Category thirdLevel = categoryRepository.findByNameAndParant(productRequest.getThirdLavelCategory(), secondLevel.getName());
        if (thirdLevel==null) {
            Category thirdLevelCategory = new Category();
            thirdLevelCategory.setName(productRequest.getThirdLavelCategory());
            thirdLevelCategory.setParentCategory(secondLevel);
            thirdLevelCategory.setLevel(3);

            thirdLevel = categoryRepository.save(thirdLevelCategory);
        }
        Product product = new Product();
        product.setTitle(productRequest.getTitle());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscountedPrice(productRequest.getDiscountedPrice());
        product.setDiscountPersent(productRequest.getDiscountPersent());
        product.setImageUrl(productRequest.getImageUrl());
        product.setQuantity(productRequest.getQuantity());
        product.setBrand(productRequest.getBrand());
        product.setColor(productRequest.getColor());
        product.setSizes(productRequest.getSize());
        product.setCategory(thirdLevel);
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        product.getSizes().clear();
        productRepository.delete(product);
        return "Product deleted Success";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        Product product = findProductById(productId);
        if (req.getQuantity()!=0) {
            product.setQuantity(req.getQuantity());
        }
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long productId) throws ProductException {
        Optional<Product> option = productRepository.findById(productId);
        if (option.isPresent()) {
            return option.get();
        }
        throw new ProductException("Product not found with id " + productId);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        return productRepository.findProductByCategory(category);
    }

    @Override
    public Page<Product> findAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);
        if (colors != null && !colors.isEmpty()) {
            products = products.stream().filter(product -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(product.getColor()))).collect(Collectors.toList());
        }
        if (stock!=null) {
            if(stock.equals("in_stock")) {
                products = products.stream().filter(product -> product.getQuantity()>0).collect(Collectors.toList());
            }
            else if(stock.equals("out_of_stock")) {
                products = products.stream().filter(product -> product.getQuantity()<1).collect(Collectors.toList());
            }
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(),products.size());
        List<Product> pageContent = products.subList(startIndex, endIndex);
        Page<Product> filteredProduct = new PageImpl<>(pageContent,pageable,products.size());
        return filteredProduct;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<CategoryProductsResponse> getLatestProductsByCategories(List<String> categories, Integer numberOfProducts) {
        List<CategoryProductsResponse> categoryProductsResponses = new ArrayList<>();

        for (String category : categories) {
            Page<Product> page = findAllProduct(category, null, null, null, null, null, "latest", null, 1, numberOfProducts);
            CategoryProductsResponse response = new CategoryProductsResponse();
            response.setName(category);
            response.setProducts(page.getContent());
            categoryProductsResponses.add(response);
        }

        return categoryProductsResponses;
    }
}
