package com.example.projectiii.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Integer bookId;
    private String bookName;
    private String author;
    private String publisher;
    private Integer pageCount;
    private String printType;
    private String language;
    private Integer quantity;
    private String bookDesc;
    private String imageUrl;
    private String cloudinaryImageId;
    private List<CategoryDTO> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private Integer categoryId;
        private String categoryName;
    }
}