package com.example.projectiii.entity;

import com.example.projectiii.constant.BorrowingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "borrowing")
@SQLDelete(sql = "UPDATE borrowing SET is_deleted = true WHERE borrowing_id = ?")
@SQLRestriction(value = "is_deleted = false")
public class Borrowing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrowing_id")
    private Long id;
    @Column(name = "borrow_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "return_date")
    private LocalDate returnDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowingType status;
    @ManyToOne
    @JoinColumn(name = "borrowing_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "borrowing_book_id")
    private Book book;

}
