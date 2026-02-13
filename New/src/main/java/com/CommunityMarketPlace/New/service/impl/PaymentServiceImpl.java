package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.PaymentRequest;
import com.CommunityMarketPlace.New.dto.PaymentResponse;
import com.CommunityMarketPlace.New.dto.VerifyPaymentRequest;
import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.OrderPayment;
import com.CommunityMarketPlace.New.repository.OrderPaymentRepository;
import com.CommunityMarketPlace.New.repository.OrderRepository;
import com.CommunityMarketPlace.New.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentServiceImpl(
            RazorpayClient razorpayClient,
            OrderPaymentRepository orderPaymentRepository,
            OrderRepository orderRepository
    ) {
        this.razorpayClient = razorpayClient;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentResponse createOrder(PaymentRequest request) {
        try {
            // Razorpay options
            JSONObject options = new JSONObject();
            options.put("amount", request.getAmount()); // amount in paise
            options.put("currency", request.getCurrency());
            options.put("receipt", "order_rcpt_" + request.getOrderId());

            // Create order in Razorpay
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

            String razorpayOrderId = razorpayOrder.get("id");

            // IMPORTANT FIX: Razorpay returns Number, not Long
            Long amount = ((Number) razorpayOrder.get("amount")).longValue();

            String currency = razorpayOrder.get("currency");

            // Load Order
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Save OrderPayment record
            OrderPayment payment = new OrderPayment();
            payment.setOrder(order);
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setStatus("CREATED");

            orderPaymentRepository.save(payment);

            // Response to frontend
            return new PaymentResponse(
                    razorpayOrderId,
                    keyId,
                    amount,
                    currency,
                    "CREATED"
            );

        } catch (RazorpayException e) {
            throw new RuntimeException("Error while creating Razorpay order", e);
        }
    }

    @Override
    public boolean verifyPayment(VerifyPaymentRequest request) {
        // Generate signature
        String generatedSignature = generateSignature(
                request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId(),
                keySecret
        );

        boolean isValid = generatedSignature.equals(request.getRazorpaySignature());

        // Update OrderPayment
        orderPaymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .ifPresent(payment -> {
                    payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
                    payment.setRazorpaySignature(request.getRazorpaySignature());
                    payment.setStatus(isValid ? "SUCCESS" : "FAILED");
                    orderPaymentRepository.save(payment);

                    // ALSO UPDATE ORDER STATUS
                    Order order = payment.getOrder();

                    if (isValid) {
                        order.setPaymentStatus("PAID");
                        order.setOrderStatus("CONFIRMED");
                    } else {
                        order.setPaymentStatus("FAILED");
                        order.setOrderStatus("PENDING_PAYMENT"); // retry allowed
                    }

                    orderRepository.save(order);
                });

        return isValid;
    }

    private String generateSignature(String data, String secret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}
