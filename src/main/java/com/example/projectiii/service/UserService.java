package com.example.projectiii.service;

import com.example.projectiii.entity.User;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.exception.UnauthorizedException;

public interface UserService {
    UserInfoResponse createUser(UserRequest request);

    boolean isCurrentUser(Long userId);

    User getCurrentUser();

    User handleGetUserByUserName(String userName);

    User handleGetUserByUserNameAndRefreshToken(String userName, String refreshToken);

    void updateUserToken(String refreshToken, String userName);

    Object getUserById(Long id);

    void deleteUserById(Long id);

    UserInfoResponse updateUser(Long id, UserRequest userRequest) throws UnauthorizedException;

    Object getAllUsers();
}
