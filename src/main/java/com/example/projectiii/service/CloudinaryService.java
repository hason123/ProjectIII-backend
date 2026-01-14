package com.example.projectiii.service;

import com.example.projectiii.dto.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    CloudinaryResponse uploadFile(final MultipartFile file, final String fileName);

    void deleteFile(final String publicId);
}
