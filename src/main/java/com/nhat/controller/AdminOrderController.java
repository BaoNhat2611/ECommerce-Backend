package com.nhat.controller;

import com.nhat.exception.OrderException;
import com.nhat.model.Order;
import com.nhat.response.ApiResponse;
import com.nhat.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/orders")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrdersHandler() {
        List<Order> orders = orderService.getAllOrder();
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @PutMapping ("/{orderId}/confirmed")
    public ResponseEntity<Order> ConfirmOrderHandler(@PathVariable("orderId")Long orderId, @RequestHeader("Authorization")String jwt) throws OrderException {
        Order order = orderService.confirmedOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping ("/{orderId}/ship")
    public ResponseEntity<Order> ShipOrderHandler(@PathVariable("orderId")Long orderId, @RequestHeader("Authorization")String jwt) throws OrderException {
        Order order = orderService.shippedOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping ("/{orderId}/deliver")
    public ResponseEntity<Order> DeliverOrderHandler(@PathVariable("orderId")Long orderId, @RequestHeader("Authorization")String jwt) throws OrderException {
        Order order = orderService.deliveredOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping ("/{orderId}/cancel")
    public ResponseEntity<Order> CancelOrderHandler(@PathVariable("orderId")Long orderId, @RequestHeader("Authorization")String jwt) throws OrderException {
        Order order = orderService.canceledOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping ("/{orderId}/delete")
    public ResponseEntity<ApiResponse> DeleteOrderHandler(@PathVariable("orderId")Long orderId, @RequestHeader("Authorization")String jwt) throws OrderException {
        orderService.deleteOrder(orderId);

        ApiResponse response = new ApiResponse();
        response.setMessage("Order deleted successfully");
        response.setStatus(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
