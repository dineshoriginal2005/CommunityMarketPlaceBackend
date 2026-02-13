package com.CommunityMarketPlace.New.service;


import com.CommunityMarketPlace.New.dto.PaymentRequest;
import com.CommunityMarketPlace.New.dto.PaymentResponse;
import com.CommunityMarketPlace.New.dto.VerifyPaymentRequest;

public interface PaymentService {

    PaymentResponse createOrder(PaymentRequest request);

    boolean verifyPayment(VerifyPaymentRequest request);
}
