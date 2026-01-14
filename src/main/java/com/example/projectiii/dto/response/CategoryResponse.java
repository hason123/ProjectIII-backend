package com.example.projectiii.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private List<BookBasic> books = new ArrayList<>();
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookBasic{
        private Integer bookId;
        private String bookName;
    }
}
