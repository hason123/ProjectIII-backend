package com.example.projectiii.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponse {
    private Long borrowingId;
    private String username;
    private String bookName;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    private String status;

}
