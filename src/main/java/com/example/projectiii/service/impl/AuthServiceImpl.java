package com.example.projectiii.service.impl;

import com.example.projectiii.dto.request.LoginRequest;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.LoginResponse;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.entity.User;
import com.example.projectiii.service.AuthService;
import com.example.projectiii.utils.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserServiceImpl userServiceImpl;


    public AuthServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserServiceImpl userServiceImpl) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        User currentUserDB = userServiceImpl.handleGetUserByUserName(request.getUsername());
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
        userLogin.setId(currentUserDB.getUserId());
        userLogin.setUsername(currentUserDB.getUserName());
        userLogin.setRole(String.valueOf(currentUserDB.getRole().getRoleName()));
        LoginResponse response = new LoginResponse();
        response.setUser(userLogin);
        // Generate tokens
        String accessToken = securityUtil.createAccessToken(authentication.getName(), response);
        String refreshToken = securityUtil.createRefreshToken(request.getUsername(), response);
        // Update refresh token in DB
        userServiceImpl.updateUserToken(refreshToken, request.getUsername());
        // Set tokens in DTO
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Override
    public LoginResponse refreshToken(String oldRefreshToken){
        Jwt decodeToken = securityUtil.checkValidRefreshToken(oldRefreshToken);
        String userName = decodeToken.getSubject();
        User user = userServiceImpl.handleGetUserByUserNameAndRefreshToken(userName, oldRefreshToken);
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                user.getUserId(),
                user.getUserName(),
                user.getRole().getRoleName().name()
        );
        LoginResponse LoginResponse = new LoginResponse();
        LoginResponse.setUser(userLogin);
        String accessToken = securityUtil.createAccessToken(userName, LoginResponse);
        LoginResponse.setAccessToken(accessToken);
        String newRefreshToken = securityUtil.createRefreshToken(userName, LoginResponse);
        LoginResponse.setRefreshToken(newRefreshToken); // This field is @JsonIgnore
        userServiceImpl.updateUserToken(newRefreshToken, userName);
        return LoginResponse;
    }

    @Override
    public UserInfoResponse register(UserRequest request) {
        return userServiceImpl.createUser(request);
    }

    @Override
    public void logout() {
        String userName = userServiceImpl.getCurrentUser().getUserName();
        userServiceImpl.updateUserToken("", userName);
    }
}

