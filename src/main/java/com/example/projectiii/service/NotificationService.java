package com.example.projectiii.service;

import com.example.projectiii.dto.response.NotificationResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void createNotification(User recipient, String title, String message, String type, String description, String actionUrl);

    int countUnread();

    List<NotificationResponse> getMyNotifications();

    PageResponse<NotificationResponse> getMyNotificationsPage(Pageable pageable);

    void markAsRead(Integer notificationId);

    void deleteNotification(Integer notificationId);
}

