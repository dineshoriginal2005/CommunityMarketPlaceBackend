package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class PaymentDetailsResponse {

    private Long id;  // payment attempt ID

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Long amount;      // in paise
    private String currency;

    private String status;    // CREATED / SUCCESS / FAILED

    private Long createdAt;
    private Long updatedAt;
}
