package com.nhat.controller;

import com.nhat.exception.OrderException;
import com.nhat.model.Order;
import com.nhat.repository.OrderRepository;
import com.nhat.response.ApiResponse;
import com.nhat.response.PaymentLinkResponse;
import com.nhat.service.OrderService;
import com.nhat.service.PaypalService;
import com.nhat.service.UserService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {


    @Autowired
    PaypalService service;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/payments/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId, @RequestHeader("Authorization") String jwt) throws OrderException, PayPalRESTException {
        Order order = orderService.findOrderById(orderId);
        try {
            Payment payment = service.createPayment(order,
                    "http://localhost:3000/payment/" + orderId,
                    "http://localhost:3000/payment/cancel/" + orderId);
            PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    paymentLinkResponse.setPayment_link_url(link.getHref());
                }
            }
            return new ResponseEntity<>(paymentLinkResponse, HttpStatus.CREATED);

        } catch (PayPalRESTException e) {
            throw new PayPalRESTException(e.getMessage());
        }
    }

    @GetMapping(value = "/payment/cancel/{orderId}")
    public String cancelPay(@PathVariable String orderId) {
        return "cancel";
    }

    @GetMapping("/payment/{orderId}")
    public ResponseEntity<ApiResponse> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @PathVariable Long orderId) throws PayPalRESTException {
        try {
            Order order = orderService.findOrderById(orderId);
            Payment payment = service.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                order.getPaymentDetails().setPaymentId(paymentId);
                order.getPaymentDetails().setStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                orderRepository.save(order);
            }
            ApiResponse res = new ApiResponse();
            res.setMessage("Your order get placed");
            res.setStatus(true);
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        } catch (PayPalRESTException e) {
            throw new PayPalRESTException(e);
        } catch (OrderException e) {
            throw new RuntimeException(e);
        }
    }
}


