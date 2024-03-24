package com.nhat.service;

import com.nhat.exception.OrderException;
import com.nhat.model.Address;
import com.nhat.model.Order;
import com.nhat.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, Address address);
    Order findOrderById(Long orderId) throws OrderException;
    List<Order> usersOrderHistory(Long userId);
    Order placedOrder(Long orderId) throws OrderException;
    Order confirmedOrder(Long orderId) throws OrderException;
    Order shippedOrder(Long orderId) throws OrderException;
    Order deliveredOrder(Long orderId) throws OrderException;
    Order canceledOrder(Long orderId) throws OrderException;
    List<Order> getAllOrder();
    void deleteOrder(Long orderId) throws OrderException;
}
