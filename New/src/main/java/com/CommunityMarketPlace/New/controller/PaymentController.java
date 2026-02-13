package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.PaymentRequest;
import com.CommunityMarketPlace.New.dto.PaymentResponse;
import com.CommunityMarketPlace.New.dto.VerifyPaymentRequest;
import com.CommunityMarketPlace.New.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // =================== CREATE RAZORPAY ORDER ===================
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody PaymentRequest request) {

        if (request.getOrderId() == null || request.getAmount() == null) {
            throw new RuntimeException("orderId and amount are required");
        }

        PaymentResponse response = paymentService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    // =================== VERIFY PAYMENT ==========================
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest request) {

        boolean isValid = paymentService.verifyPayment(request);

        if (isValid) {
            return ResponseEntity.ok(
                    "{\"message\":\"Payment successful\", \"orderId\": \""
                            + request.getRazorpayOrderId() + "\"}"
            );
        } else {
            return ResponseEntity.badRequest().body(
                    "{\"message\":\"Payment verification failed\"}"
            );
        }
    }
}
