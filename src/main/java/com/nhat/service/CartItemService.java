package com.nhat.service;

import com.nhat.exception.CartItemException;
import com.nhat.exception.UserException;
import com.nhat.model.Cart;
import com.nhat.model.CartItem;
import com.nhat.model.Product;

public interface CartItemService {
    CartItem createCartItem(CartItem cartItem);
    CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException;
    CartItem isCartItemExist(Cart cart, Product product, String size, Long userId);
    void removeCartItem(Long userId, Long cartItemId) throws CartItemException,UserException;
    CartItem findCartItemById(Long cartItemId) throws CartItemException;
}
