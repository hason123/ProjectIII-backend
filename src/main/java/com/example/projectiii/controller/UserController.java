package com.example.projectiii.controller;

import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.dto.response.ApiResponse;
import com.example.projectiii.entity.User;
import com.example.projectiii.exception.UnauthorizedException;
import com.example.projectiii.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) throws UnauthorizedException {
        Object user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) throws UnauthorizedException {
        userService.deleteUserById(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<UserInfoResponse> createUser(@Valid @RequestBody UserRequest user) {
        UserInfoResponse userAdded = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdded);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserInfoResponse> updateUser( @PathVariable Integer id,
                                                           @Valid @RequestBody UserRequest user) throws UnauthorizedException {
        UserInfoResponse updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserInfoResponse>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<UserInfoResponse> userPage = userService.getUserPage(pageable);
        return ResponseEntity.ok(userPage); // HTTP 200 + JSON list
    }


}
