package com.example.projectiii.service.impl;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.entity.User;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.exception.UnauthorizedException;
import com.example.projectiii.repository.RoleRepository;
import com.example.projectiii.repository.UserRepository;
import com.example.projectiii.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserInfoResponse createUser(UserRequest request){
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleType.USER));
        user.setBirthday(request.getBirthday());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStudentNumber(request.getStudentNumber());
        user.setFullName(request.getFullName());
        userRepository.save(user);
        return convertUserInfoToDTO(user);
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String currentUserName = jwt.getClaim("sub"); // decode token de lay phan sub
            Long currentUserId = userRepository.findByUserName(currentUserName).getUserId();
            return currentUserId.equals(userId);
        }
        return false;
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String currentUserName = jwt.getClaim("sub"); // decode token de lay phan sub
            return userRepository.findByUserName(currentUserName);
        }
        return null;
    }

    @Override
    public User handleGetUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User handleGetUserByUserNameAndRefreshToken(String userName, String refreshToken) {
        return userRepository.findByUserNameAndRefreshToken(userName, refreshToken);
    }

    @Override
    public void updateUserToken(String refreshToken, String userName) {
        User currentUser = handleGetUserByUserName(userName);
        if(currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new ResourceNotFoundException("User not found");
        }
        if(isCurrentUser(id) || getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN)) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public UserInfoResponse updateUser(Long id, UserRequest request) throws UnauthorizedException {
        User updatedUser = userRepository.findById(id).orElse(null);
        if(!isCurrentUser(id) ){
            throw new UnauthorizedException("You have no permission");
        }
        if(updatedUser == null){
            throw new ResourceNotFoundException("User not found");
        }
        if (request.getUserName() != null) {
            updatedUser.setUserName(request.getUserName());
        }
        else{
            updatedUser.setUserName(updatedUser.getUserName());
        }
        if (request.getPassword() != null) {
            updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        else updatedUser.setPassword(updatedUser.getPassword());
        if (request.getBirthday() != null) {
            updatedUser.setBirthday(request.getBirthday());
        }
        if (request.getAddress() != null) {
            updatedUser.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            updatedUser.setPhoneNumber(request.getPhoneNumber());
        }
        else updatedUser.setPhoneNumber(updatedUser.getPhoneNumber());
        if (request.getStudentNumber() != null) {
            updatedUser.setStudentNumber(request.getStudentNumber());
        }
        else updatedUser.setStudentNumber(updatedUser.getStudentNumber());
        userRepository.save(updatedUser);
        return convertUserInfoToDTO(updatedUser);
    }

    @Override
    public Object getUserById(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new ResourceNotFoundException("User not found");
        }
        return convertUserInfoToDTO(user);
    }

    @Override
    public Object getAllUsers() {
        List<UserInfoResponse> users = userRepository.findAll().stream().map(this::convertUserInfoToDTO).collect(Collectors.toList());
        return users;
    }

    public UserInfoResponse convertUserInfoToDTO(User user){
        UserInfoResponse userDTO = new UserInfoResponse();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setStudentNumber(user.getStudentNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFullName(user.getFullName());
        userDTO.setPassword("HIDDEN");
        userDTO.setRoleName(user.getRole().getRoleName().toString());
        return userDTO;
    }


}
