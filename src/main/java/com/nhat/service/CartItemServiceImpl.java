package com.nhat.service;

import com.nhat.exception.CartItemException;
import com.nhat.exception.UserException;
import com.nhat.model.Cart;
import com.nhat.model.CartItem;
import com.nhat.model.Product;
import com.nhat.model.User;
import com.nhat.repository.CartItemRepository;
import com.nhat.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService{
    private CartItemRepository cartItemRepository;
    private UserService userService;
    private CartRepository cartRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, UserService userService, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        if (cartItem != null && cartItem.getProduct() != null) {
            cartItem.setQuantity(1);
            cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());

            return cartItemRepository.save(cartItem);
        } else {
            throw new IllegalArgumentException("Invalid cart item data");
        }
    }

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {
        CartItem item = findCartItemById(id);

        if (item != null) {
            User user = userService.findUserById(item.getUserId());
            if (user.getId().equals(userId)) {
                item.setQuantity(cartItem.getQuantity());
                item.setPrice(item.getProduct().getPrice() * item.getQuantity());
                item.setDiscountedPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
                return cartItemRepository.save(item);
            } else {
                throw new UserException("You can only update your own cart item");
            }
        } else {
            throw new CartItemException("Cart item not found");
        }
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {
        CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, userId);
        return cartItem;
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
        CartItem cartItem = findCartItemById(cartItemId);
        User reqUser = userService.findUserById(userId);

        if (cartItem != null) {
            User user = userService.findUserById(cartItem.getUserId());
            if (user.getId().equals(reqUser.getId())) {
                cartItemRepository.delete(cartItem);
            } else {
                throw new UserException("You can't remove another user's item");
            }
        } else {
            throw new CartItemException("Cart item not found");
        }
    }

    @Override
    public CartItem findCartItemById(Long cartItemId) throws CartItemException {
        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new CartItemException("cart item not found with id - " + cartItemId);
    }
}
