package com.example.projectiii.service.impl;

import com.cloudinary.Cloudinary;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.exception.BusinessException;
import com.example.projectiii.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Transactional
    public CloudinaryResponse uploadFile(final MultipartFile file, final String fileName) {
        try {
            Map<String, Object> options = Map.of("public_id", fileName);
            Map result = cloudinary.uploader().upload(file.getBytes(), options);;
            final String url      = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder().publicId(publicId).url(url)
                    .build();

        } catch (final Exception e) {
            throw new BusinessException("Failed to upload file");
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try{
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (final Exception e) {
            throw new BusinessException("Failed to delete file");
        }
    }
}
