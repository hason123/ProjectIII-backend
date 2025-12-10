package com.example.projectiii.service;

import com.example.projectiii.dto.request.LoginRequest;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.LoginResponse;
import com.example.projectiii.dto.response.user.UserInfoResponse;


public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout();

    UserInfoResponse register(UserRequest request);
}
