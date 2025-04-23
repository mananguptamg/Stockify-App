package com.app.stock.dto;

import lombok.Data;

@Data
public class OTPVerificationRequestDTO {
    private String email;
    private String otp;
}
