package com.example.projectiii.dto.response;

import com.example.projectiii.constant.BorrowingStatus;
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
    private Integer borrowingId;
    private Integer bookId;
    private Integer userId;
    private String username;
    private String bookName;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private String status;
    private String fullName;


}
