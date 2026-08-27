package com.campusfound.image.service;

import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageKitClient imageKitClient;

    public String uploadItemImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        try {

            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isBlank()) {
                fileName = "campusfound-item.jpg";
            }

            FileUploadParams params = FileUploadParams.builder()
                    .file(file.getBytes())
                    .fileName(fileName)
                    .folder("/campusfound/items")
                    .addTag("campusfound")
                    .addTag("item")
                    .build();

            FileUploadResponse response =
                    imageKitClient.files().upload(params);

            return response.url()
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "ImageKit did not return an image URL"
                            )
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to upload image to ImageKit",
                    e
            );
        }
    }
}