package com.nhat.response;

import com.nhat.model.Product;

import java.util.List;

public class CategoryProductsResponse {
    private String name;
    private List<Product> products;

    public CategoryProductsResponse() {
    }

    public CategoryProductsResponse(String name, List<Product> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
