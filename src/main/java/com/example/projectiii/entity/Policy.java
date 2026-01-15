package com.example.projectiii.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "policy")
public class Policy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "max_books")
    private Integer maxBooks;

    @Column(name = "borrow_days")
    private Integer borrowDays;

    @Column(name = "max_renew_count")
    private Integer maxRenewCount;

/*    // Số ngày được gia hạn mỗi lần
    @Column(name = "renew_days")
    private Integer renewDays;*/

/*
    // Phạt / ngày trễ
    @Column(name = "fine_per_day")
    private Double finePerDay;

    // Có cần thủ thư duyệt gia hạn không
    @Column(name = "renew_need_approval")
    private Boolean renewNeedApproval;

    // Có cần thủ thư duyệt mượn không
    @Column(name = "borrow_need_approval")
    private Boolean borrowNeedApproval;
*/

    // Trạng thái
    @Column(name = "active")
    private Boolean active;
}
