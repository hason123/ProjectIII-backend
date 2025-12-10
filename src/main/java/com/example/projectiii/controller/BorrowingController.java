package com.example.projectiii.controller;

import com.example.projectiii.dto.request.BorrowingRequest;
import com.example.projectiii.dto.response.BorrowingResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.service.BorrowingService;
//import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/borrows")
    public ResponseEntity<?> getBorrowingPage(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize); //da sort trong lop service
        PageResponse<BorrowingResponse> borrowingPage = borrowingService.getBorrowingPage(pageable);
        return ResponseEntity.ok(borrowingPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/borrows/{id}")
    public ResponseEntity<BorrowingResponse> getBorrowingById(@PathVariable Long id) throws ResourceNotFoundException {
        BorrowingResponse borrowing = borrowingService.getBorrowingById(id);
        return ResponseEntity.status(HttpStatus.OK).body(borrowing);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/borrows")
    public ResponseEntity<BorrowingResponse> addBorrowing(@Valid @RequestBody BorrowingRequest borrowing) {
        BorrowingResponse borrowingAdded = borrowingService.addBorrowing(borrowing);
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowingAdded);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/borrows/{id}")
    public ResponseEntity<BorrowingResponse> updateBorrowing(@PathVariable Long id, @Valid @RequestBody BorrowingRequest borrowing) {
        BorrowingResponse borrowingUpdated = borrowingService.updateBorrowing(id, borrowing);
        return ResponseEntity.ok(borrowingUpdated);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/borrows/{id}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowingById(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/borrows/dashboard")
    public ResponseEntity<?> getBorrowingDashboard(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "attachment; filename=borrowing.xlsx");
        borrowingService.createBorrowingWorkbook(response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
