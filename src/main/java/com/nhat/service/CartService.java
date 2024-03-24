package com.nhat.service;

import com.nhat.exception.CartItemException;
import com.nhat.exception.ProductException;
import com.nhat.exception.UserException;
import com.nhat.model.Cart;
import com.nhat.model.CartItem;
import com.nhat.model.User;
import com.nhat.request.AddCartRequest;

import java.util.List;

public interface CartService {
    Cart createCart(User user);
    List<CartItem> addCartItem(Long userId, AddCartRequest request) throws ProductException, CartItemException, UserException;
    Cart findUserCart(Long userId);
    public List<CartItem> addCartItems(Long userId, List<AddCartRequest> cartItems) throws ProductException, CartItemException, UserException;
}
