package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {

    private Long orderId;              // Internal order ID
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
