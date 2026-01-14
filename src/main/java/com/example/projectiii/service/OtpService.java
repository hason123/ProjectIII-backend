package com.example.projectiii.service;

import com.example.projectiii.constant.OtpType;
import com.example.projectiii.entity.Otp;
import com.example.projectiii.entity.User;

public interface OtpService {
    void sendOtpEmail(String toGmail, String otpCode);

    Otp createOtp(User user, OtpType type);

    boolean validateOtp(User user, String code, OtpType type);

    void resendOtp(User user, OtpType type);
}
