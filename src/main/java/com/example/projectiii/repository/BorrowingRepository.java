package com.example.projectiii.repository;

import com.example.projectiii.constant.BorrowingStatus;
import com.example.projectiii.entity.Book;
import com.example.projectiii.entity.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRepository  extends JpaRepository<Borrowing, Integer> {

/*    @Query("SELECT b FROM Borrowing b ORDER BY CASE WHEN b.returnDate IS NULL THEN 0 ELSE 1 END ASC, b.returnDate DESC")
    Page<Borrowing> findAllCustomSort(Pageable pageable);

    @Query("SELECT b.book FROM Borrowing b WHERE b.status = com.example.projectiii.constant.BorrowingStatus.BORROWED GROUP BY b.book ORDER BY COUNT(b) DESC")
    List<Book> findCurrentBorrowingBooks();

    @Query("SELECT b FROM Borrowing b WHERE b.returnDate IS NULL")
    List<Borrowing> findByStatusBorrowingOrDue();*/

    List<Borrowing> findByStatus(BorrowingStatus borrowingType);

    boolean existsByUserIdAndBook_BookIdAndStatus(int userId, int bookId, BorrowingStatus borrowingStatus);

    Borrowing findByUserIdAndBook_BookIdAndStatus(int userId, int bookId, BorrowingStatus borrowingStatus);
}
