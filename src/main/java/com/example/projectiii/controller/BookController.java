package com.example.projectiii.controller;

import com.example.projectiii.dto.request.BookRequest;
import com.example.projectiii.dto.request.search.SearchBookRequest;
import com.example.projectiii.dto.response.BookResponse;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.service.BookService;
import com.example.projectiii.service.impl.BookServiceImpl;
//import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books")
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
        PageResponse<BookResponse> bookPage = bookService.getBookPage(pageable);
        return ResponseEntity.ok(bookPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books/user/borrowing")
    public ResponseEntity<PageResponse<BookResponse>> getBooksBorrowingStudent(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
        PageResponse<BookResponse> bookPage = bookService.getBooksBorrowingStudent(pageable);
        return ResponseEntity.ok(bookPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("id") Integer id) {
        BookResponse bookResponse = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBook(
            SearchBookRequest request,
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<BookResponse> bookSearch = bookService.searchBooks(request, pageable);
        return ResponseEntity.ok(bookSearch);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest book) {
        BookResponse bookAdded = bookService.addBook(book);
        return ResponseEntity.status(201).body(bookAdded);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("id") Integer id, @RequestBody BookRequest book) {
        BookResponse bookUpdated = bookService.updateBook(id, book);
        return ResponseEntity.status(200).body(bookUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Integer id) {
        bookService.deleteBookById(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/books/export")
    public ResponseEntity<?> exportBook(final HttpServletResponse response) throws IOException{
        response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");
        bookService.exportBookWorkbook(response);
        Map<String, String> message = Map.of("message", "Export successful!");
        return ResponseEntity.ok(message);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/books/import")
    public ResponseEntity<?> importBook(@RequestPart final MultipartFile file) throws IOException {
        bookService.importExcel(file);
        Map<String, String> message = Map.of("message", "Import successful!");
        return ResponseEntity.ok(message);
    }

    @PutMapping("/books/{id}/image")
    public ResponseEntity<CloudinaryResponse> uploadImage(@PathVariable final Integer id, @RequestPart final MultipartFile file) {
        CloudinaryResponse bookImage = bookService.uploadImage(id, file);
        return ResponseEntity.ok(bookImage);
    }



}
