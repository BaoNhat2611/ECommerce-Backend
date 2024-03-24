package com.nhat.service;

import com.nhat.model.Order;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaypalServiceImpl implements PaypalService {

	@Autowired
	private APIContext apiContext;


	public Payment createPayment(Order order, String successUrl, String cancelUrl) throws PayPalRESTException {
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Transaction transaction = createTransaction(order);

		Payment payment = new Payment();
		payment.setIntent("sale")
				.setPayer(payer)
				.setTransactions(Collections.singletonList(transaction))
				.setRedirectUrls(new RedirectUrls().setCancelUrl(cancelUrl).setReturnUrl(successUrl));

		System.out.println("Payment Request Payload: " + payment.toJSON());

		return payment.create(apiContext);
	}

	private Transaction createTransaction(Order order) {
		return (Transaction) new Transaction()
				.setDescription("Payment for Order #" + order.getId())
				.setAmount(createAmount(order))
				.setItemList(createItemList(order));
	}

	private Amount createAmount(Order order) {
		double totalAmount = order.getOrderItems().stream()
				.mapToDouble(orderItem -> orderItem.getQuantity() * orderItem.getPrice())
				.sum();

		DecimalFormat df = new DecimalFormat("0.00");

		Amount amount = new Amount()
				.setCurrency("USD")
				.setTotal(df.format(totalAmount))
				.setDetails(new Details().setSubtotal(df.format(totalAmount)).setShipping("0.00"));

		return amount;
	}

	private ItemList createItemList(Order order) {
		List<Item> items = order.getOrderItems().stream()
				.map(orderItem -> new Item()
						.setName(orderItem.getProduct().getTitle())
						.setDescription(orderItem.getProduct().getDescription())
						.setQuantity(String.valueOf(orderItem.getQuantity()))
						.setPrice(new DecimalFormat("0.00").format(orderItem.getPrice()))
						.setCurrency("USD"))
				.collect(Collectors.toList());

		return new ItemList().setItems(items);
	}

	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

}
