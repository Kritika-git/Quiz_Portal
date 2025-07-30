package com.cmpdi.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService() {
        // Initialize with environment variables
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
            "api_key", System.getenv("CLOUDINARY_API_KEY"),
            "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));
        
        System.out.println("✅ Cloudinary initialized with cloud: " + System.getenv("CLOUDINARY_CLOUD_NAME"));
    }
    
    public String uploadImage(MultipartFile file) {
        try {
            System.out.println("🔄 Uploading to Cloudinary: " + file.getOriginalFilename());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "quiz_images",  // Organize images in a folder
                    "use_filename", true,
                    "unique_filename", true
                )
            );
            
            String imageUrl = (String) uploadResult.get("secure_url");
            System.out.println("✅ Cloudinary upload successful: " + imageUrl);
            
            return imageUrl;
            
        } catch (Exception e) {
            System.err.println("❌ Cloudinary upload failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage());
        }
    }
}