package com.example.projectiii.controller;

import com.example.projectiii.dto.request.BorrowingRequest;
import com.example.projectiii.dto.response.BorrowingResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.service.BorrowingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@Tag(name = "Borrowing Management", description = "APIs for managing book borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    @Operation(summary = "Sinh viên gửi yêu cầu mượn sách")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/books/{bookId}/borrow")
    public ResponseEntity<BorrowingResponse> requestBorrowing(
            @PathVariable Integer bookId) {
        BorrowingResponse response = borrowingService.requestBorrowing(bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Thủ thư duyệt yêu cầu mượn sách")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @PostMapping("/borrowings/approve")
    public ResponseEntity<BorrowingResponse> approveBorrowing(
            @RequestBody BorrowingRequest request) {
        BorrowingResponse response = borrowingService.approveBorrowing(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @PostMapping("/borrowings/{id}")
    public ResponseEntity<BorrowingResponse> updateBorrowing(@PathVariable Integer id,
            @RequestBody BorrowingRequest request) {
        BorrowingResponse response = borrowingService.updateBorrowing(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thủ thư từ chối yêu cầu mượn sách")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @DeleteMapping("/borrowings/reject")
    public ResponseEntity<Void> rejectBorrowing(
            @RequestBody BorrowingRequest request) {
        borrowingService.rejectBorrowing(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy chi tiết một lượt mượn sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/borrowings/{id}")
    public ResponseEntity<BorrowingResponse> getBorrowingById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(borrowingService.getBorrowingById(id));
    }

    @Operation(summary = "Lấy danh sách tất cả lượt mượn (Admin)")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/borrowings")
    public ResponseEntity<PageResponse<BorrowingResponse>> getBorrowingPage(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return ResponseEntity.ok(borrowingService.getBorrowingPage(pageable));
    }

    @Operation(summary = "Xóa một lượt mượn sách")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @DeleteMapping("/borrowings/{id}")
    public ResponseEntity<Void> deleteBorrowing(
            @PathVariable Integer id) {
        borrowingService.deleteBorrowingById(id);
        return ResponseEntity.noContent().build();
    }
}
