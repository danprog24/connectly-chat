package com.dannycode.chatApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadAvatar(MultipartFile file, String username) throws IOException {
        System.out.println("=== Cloudinary upload starting for: " + username);
        System.out.println("=== Cloud name: " + cloudinary.config.cloudName);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "connectly/avatars",
                    "public_id", "avatar_" + username,
                    "overwrite", true
                    // "transformation", ObjectUtils.asMap(
                    //     "width", 200,
                    //     "height", 200,
                    //     "crop", "fill",
                    //     // "gravity", "face"
                    //     "transformation", "w_200,h_200,c_fill,g_face"
                    // )
                )
            );
            System.out.println("=== Cloudinary upload success: " + result.get("secure_url"));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            System.out.println("=== Cloudinary upload failed: " + e.getMessage());
            throw e;
        }
    }
}