package com.example.projectiii.entity;

import com.example.projectiii.constant.BorrowingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.List;

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
    private Integer id;
    @Column(name = "borrow_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate;
    @Column(name = "due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    @Column(name = "returned_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnedDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;
    @ManyToOne
    @JoinColumn(name = "borrowing_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "borrowing_book_id")
    private Book book;
    @Column(name = "renew_count")
    private Integer renewCount = 0;
    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL)
    private List<RenewRequest> renewRequests;

}
