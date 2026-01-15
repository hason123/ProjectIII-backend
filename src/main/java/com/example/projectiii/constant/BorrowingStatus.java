package com.example.projectiii.constant;

public enum BorrowingStatus {
    PENDING,   // Chờ duyệt mượn
    BORROWING,          // Đang mượn
    RENEW_PENDING,      // Đang chờ duyệt gia hạn (quan trọng)
    RETURNED,           // Đã trả
    OVERDUE,            // Quá hạn
    REJECTED,           // Bị từ chối mượn
    CANCELLED           // Hủy
}
