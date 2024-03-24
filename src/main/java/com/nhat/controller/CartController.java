package com.nhat.controller;

import com.nhat.exception.CartItemException;
import com.nhat.exception.ProductException;
import com.nhat.exception.UserException;
import com.nhat.model.Cart;
import com.nhat.model.CartItem;
import com.nhat.model.User;
import com.nhat.request.AddCartRequest;
import com.nhat.service.CartService;
import com.nhat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization")String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        Cart cart = cartService.findUserCart(user.getId());
        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<List<CartItem>> addItemtoCart(@RequestBody AddCartRequest req,
                                                     @RequestHeader("Authorization")String jwt) throws UserException, ProductException, CartItemException {
        User user = userService.findUserProfileByJwt(jwt);
        List<CartItem> cartItems = cartService.addCartItem(user.getId(), req);

//        ApiResponse response = new ApiResponse();
//        response.setMessage("item added to cart");
//        response.setStatus(true);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @PostMapping("/addItems")
    public List<CartItem> addCartItems(@RequestBody List<AddCartRequest> cartItems,@RequestHeader("Authorization")String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            return cartService.addCartItems(user.getId(), cartItems);
        } catch (ProductException | CartItemException | UserException e) {
            // Xử lý lỗi nếu cần thiết
            e.printStackTrace(); // Ví dụ: In ra lỗi vào log
            return null; // Hoặc trả về một thông báo lỗi khác
        }
    }
}
