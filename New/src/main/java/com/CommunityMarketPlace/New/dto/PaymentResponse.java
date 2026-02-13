package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String razorpayOrderId;
    private String keyId;
    private Long amount;     // in paise
    private String currency;
    private String status;   // CREATED / SUCCESS / FAILED
}
