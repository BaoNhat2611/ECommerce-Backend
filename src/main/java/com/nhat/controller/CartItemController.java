package com.nhat.controller;

import com.nhat.exception.CartItemException;
import com.nhat.exception.UserException;
import com.nhat.model.CartItem;
import com.nhat.model.User;
import com.nhat.response.ApiResponse;
import com.nhat.service.CartItemService;
import com.nhat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartItem")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UserService userService;

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable Long cartItemId, @RequestHeader("Authorization")String jwt) throws UserException, CartItemException {
        User user = userService.findUserProfileByJwt(jwt);
        cartItemService.removeCartItem(user.getId(), cartItemId);

        ApiResponse res = new ApiResponse();
        res.setMessage("item deleted to cart");
        res.setStatus(true);
        return new ResponseEntity<ApiResponse>(res, HttpStatus.OK);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartItemId, @RequestHeader("Authorization")String jwt,@RequestBody CartItem req) throws UserException, CartItemException {
        User user = userService.findUserProfileByJwt(jwt);
        CartItem cartItem = cartItemService.updateCartItem(user.getId(), cartItemId, req);
        return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
    }

}
