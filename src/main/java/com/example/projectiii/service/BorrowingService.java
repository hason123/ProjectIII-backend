package com.example.projectiii.service;

import com.example.projectiii.dto.request.BorrowingRequest;
import com.example.projectiii.dto.response.BorrowingResponse;
import com.example.projectiii.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface BorrowingService {

    void deleteBorrowingById(Long id);

    BorrowingResponse addBorrowing(BorrowingRequest request);

    BorrowingResponse getBorrowingById(Long id);

    BorrowingResponse updateBorrowing(Long id, BorrowingRequest request);

    PageResponse<BorrowingResponse> getBorrowingPage(Pageable pageable);

    void createBorrowingWorkbook(HttpServletResponse response) throws IOException;

}
