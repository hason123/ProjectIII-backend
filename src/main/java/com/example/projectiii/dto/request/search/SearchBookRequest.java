package com.example.projectiii.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchBookRequest {
    private Long bookId;
    private String bookName;
    private String author;
    private String publisher;
    private Integer pageCount;
    private String printType;
    private String language;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Integer minPageCount;
    private Integer maxPageCount;
    private String bookDesc;
    private List<String> categories;
}
