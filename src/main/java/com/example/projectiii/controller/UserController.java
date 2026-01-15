package com.example.projectiii.controller;

import com.example.projectiii.dto.request.UserRequest;
import com.example.projectiii.dto.request.search.SearchUserRequest;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.dto.response.user.UserInfoResponse;
import com.example.projectiii.dto.response.ApiResponse;
import com.example.projectiii.entity.User;
import com.example.projectiii.exception.UnauthorizedException;
import com.example.projectiii.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Lấy thông tin người dùng theo ID")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok((UserInfoResponse) userService.getUserById(id));
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserInfoResponse> updateUser(@PathVariable Integer id, @RequestBody UserRequest request) {
        UserInfoResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Tạo người dùng mới (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<UserInfoResponse> createUser(@RequestBody UserRequest request) {
        UserInfoResponse newUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(summary = "Xóa người dùng (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /*@Operation(summary = "Tạo người dùng Google")
    @PreAuthorize("permitAll()")
    @PostMapping("/users/google")
    public ResponseEntity<UserInfoResponse> createGoogleUser(@RequestParam String email, @RequestParam String username) {
        User newGoogleUser = userService.createGoogleUser(email, username);
        UserInfoResponse newGoogleUserDTO = userService.convertUserInfoToDTO(newGoogleUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGoogleUserDTO);
    }*/

    @Operation(summary = "Lấy danh sách người dùng có phân trang")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserInfoResponse>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<UserInfoResponse> userPage = userService.getUserPage(pageable);
        return ResponseEntity.ok(userPage);
    }

    @Operation(summary = "Tải lên ảnh đại diện người dùng")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/users/{id}/avatar")
    public ResponseEntity<CloudinaryResponse> uploadImage(@PathVariable final Integer id, @RequestPart final MultipartFile file) {
        CloudinaryResponse response = userService.uploadImage(id, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tìm kiếm người dùng")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/users/search")
    public ResponseEntity<PageResponse<UserInfoResponse>> searchUsers(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize, SearchUserRequest request){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<UserInfoResponse> results = userService.searchUser(request,pageable);
        return ResponseEntity.ok(results);
    }

}
