package com.example.projectiii.service;

import com.example.projectiii.dto.request.LoginRequest;
import com.example.projectiii.dto.request.OtpVerificationRequest;
import com.example.projectiii.dto.request.ResetPasswordRequest;
import com.example.projectiii.dto.request.ChangePasswordRequest;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.LoginResponse;
import com.example.projectiii.dto.response.user.UserInfoResponse;


public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String oldRefreshToken);

    void logout();

    Integer register(UserRequest request);

    LoginResponse verifyOtp(OtpVerificationRequest request);

    void changePassword(ChangePasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void resetPasswordVerification(String gmail);

    void resendRegisterOtp(String gmail);

    void resendResetPasswordOtp(String gmail);

    //LoginResponse googleLogin(GoogleLoginRequest request);
}
