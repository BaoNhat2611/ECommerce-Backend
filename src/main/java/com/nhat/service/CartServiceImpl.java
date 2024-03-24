package com.nhat.service;

import com.nhat.exception.CartItemException;
import com.nhat.exception.ProductException;
import com.nhat.exception.UserException;
import com.nhat.model.Cart;
import com.nhat.model.CartItem;
import com.nhat.model.Product;
import com.nhat.model.User;
import com.nhat.repository.CartRepository;
import com.nhat.request.AddCartRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{
    private CartRepository cartRepository;
    private CartItemService cartItemService;
    private ProductService productService;

    public CartServiceImpl(CartRepository cartRepository, CartItemService cartItemService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public List<CartItem> addCartItem(Long userId, AddCartRequest request) throws ProductException, CartItemException, UserException {
        Cart cart = cartRepository.findByUserId(userId);
        Product product = productService.findProductById(request.getProductId());
        CartItem isPresent = cartItemService.isCartItemExist(cart, product, request.getSize(), userId);
        if (isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setProduct(product);
            cartItem.setUserId(userId);

            int price = product.getDiscountedPrice()* request.getQuantity();
            cartItem.setPrice(price);
            cartItem.setSize(request.getSize());
            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
        } else {
            isPresent.setQuantity(request.getQuantity() + isPresent.getQuantity());
            cartItemService.updateCartItem(userId, isPresent.getId(), isPresent);
        }
        return cart.getCartItems().stream().toList();
    }

    @Override
    public List<CartItem> addCartItems(Long userId, List<AddCartRequest> cartItems) throws ProductException, CartItemException, UserException {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart items list is null or empty");
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new CartItemException("Cart not found for the user");
        }

        List<CartItem> updatedCartItems = new ArrayList<>();

        for (AddCartRequest request : cartItems) {
            if (request == null || request.getProductId() == null || request.getSize() == null || request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid add cart request data");
            }

            Product product = productService.findProductById(request.getProductId());
            if (product == null) {
                throw new ProductException("Product not found");
            }

            CartItem existingCartItem = cartItemService.isCartItemExist(cart, product, request.getSize(), userId);
            if (existingCartItem == null) {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setQuantity(request.getQuantity());
                cartItem.setProduct(product);
                cartItem.setUserId(userId);

                int price = product.getDiscountedPrice() * request.getQuantity();
                cartItem.setPrice(price);
                cartItem.setSize(request.getSize());

                CartItem createdCartItem = cartItemService.createCartItem(cartItem);
                updatedCartItems.add(createdCartItem);
            } else {
                existingCartItem.setQuantity(request.getQuantity() + existingCartItem.getQuantity());
                cartItemService.updateCartItem(userId, existingCartItem.getId(), existingCartItem);
                updatedCartItems.add(existingCartItem);
            }
        }

        return updatedCartItems;
    }


    @Override
    public Cart findUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        double totalPrice = 0;
        double totalDiscountedPrice = 0;
        int totalItem = 0;
        for (CartItem cartItem: cart.getCartItems()) {
            totalPrice=totalPrice+cartItem.getPrice();
            totalDiscountedPrice=totalDiscountedPrice+cartItem.getDiscountedPrice();
            totalItem=totalItem+cartItem.getQuantity();
        }
        cart.setTotalPrice(totalPrice);
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setDiscount(totalPrice-totalDiscountedPrice);
        return cartRepository.save(cart);
    }
}
