package com.example.projectiii.service;

import com.example.projectiii.dto.request.BorrowingRequest;
import com.example.projectiii.dto.response.BorrowingResponse;
import com.example.projectiii.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

public interface BorrowingService {
    @Transactional
    BorrowingResponse requestBorrowing(Integer bookId);

    @Transactional
    BorrowingResponse approveBorrowing(BorrowingRequest request);

    @Transactional
    void rejectBorrowing(BorrowingRequest request);

    void deleteBorrowingById(Integer id);

    @Scheduled(cron = "0 0 8 * * *") // Chạy 8:00 sáng hàng ngày
    @Transactional
    void scanOverdueBorrowings();

    BorrowingResponse getBorrowingById(Integer id);

    BorrowingResponse updateBorrowing(Integer id, BorrowingRequest request);

    PageResponse<BorrowingResponse> getBorrowingPage(Pageable pageable);
}
