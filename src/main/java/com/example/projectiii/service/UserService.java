package com.example.projectiii.service;

import com.example.projectiii.dto.request.search.SearchUserRequest;
import com.example.projectiii.dto.response.BookResponse;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.user.UserViewResponse;
import com.example.projectiii.entity.User;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.exception.UnauthorizedException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User handleGetUserByGmail(String email);

    User handleGetUserByUserName(String name);

    boolean isCurrentUser(Integer id);

    User getCurrentUser();

    User handleGetUserByUserNameAndRefreshToken(String userName, String refreshToken);

    void updateUserToken(String refreshToken, String userName);

    void deleteUserById(Integer id);

    User createGoogleUser(String email, String name);

    UserInfoResponse updateUser(Integer id, UserRequest request);

    Object getUserById(Integer id);

    PageResponse<UserInfoResponse> getUserPage(Pageable pageable);

    UserInfoResponse registerUser(UserRequest request);

    UserInfoResponse createUser(UserRequest request);

    PageResponse<UserInfoResponse> searchUser(SearchUserRequest request, Pageable pageable);

    void initiateEmailVerification(String gmail);

    void resetPasswordVerification(String gmail);

    UserInfoResponse convertUserInfoToDTO(User user);

    CloudinaryResponse uploadImage(final Integer id, final MultipartFile file);

    UserViewResponse convertUserViewToDTO(User user);
}
