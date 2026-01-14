package com.example.projectiii.repository;

import com.example.projectiii.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> , JpaSpecificationExecutor<Book> {
/*
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.bookId) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookDesc) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<BookResponse> findBooksBySearchText(@Param("searchText") String keyword);
*/
    boolean existsByBookName(String title);

}
