package com.example.projectiii.service;

import com.example.projectiii.dto.request.BookRequest;
import com.example.projectiii.dto.request.search.SearchBookRequest;
import com.example.projectiii.dto.response.BookResponse;
import com.example.projectiii.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BookService {

    BookResponse addBook(BookRequest request);

    BookResponse getBookById(Long id);

    void deleteBookById(Long id);

    BookResponse updateBook(Long id, BookRequest request);

    PageResponse<BookResponse> getBookPage(Pageable pageable);

    PageResponse<BookResponse> searchBooks(SearchBookRequest searchBookRequest,
                                                 Pageable pageable);

    void exportBookWorkbook(HttpServletResponse response) throws IOException;

    void importExcel(MultipartFile file) throws IOException;
}
