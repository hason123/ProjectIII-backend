package com.example.projectiii.entity;

import com.example.projectiii.constant.BorrowingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "renew_request")
@SQLDelete(sql = "UPDATE renew_request SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted = false")
public class RenewRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "borrowing_id", nullable = false)
    private Borrowing borrowing;

    @Column(name = "old_due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate oldDueDate;

    @Column(name = "new_due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate newDueDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;

    // Lý do sinh viên xin gia hạn
    @Column(name = "student_reason")
    private String studentReason;

    // Lý do thủ thư từ chối (quan trọng để hiển thị lại cho SV)
    @Column(name = "librarian_note")
    private String librarianNote;

    // Người duyệt (Thủ thư nào đã duyệt)
    @Column(name = "approved_by")
    private Integer approvedBy;
}