package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    // Your internal Order ID
    private Long orderId;

    // Amount in paise (₹100 → 10000)
    private Long amount;

    private String currency;   // INR
    private String receipt;    // optional
}
