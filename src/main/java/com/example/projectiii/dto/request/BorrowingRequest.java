package com.example.projectiii.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequest {
    @NotNull(message = "{error.borrowing.date.null}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "{error.borrowing.date.invalid}")
    private LocalDate borrowingDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "{error.borrowing.date.invalid}")
    private LocalDate returnDate;
    @NotNull(message = "{error.borrowing.userId.null}")
    private Long userId;
    @NotNull(message = "{error.borrowing.bookId.null}")
    private Long bookId;
}
