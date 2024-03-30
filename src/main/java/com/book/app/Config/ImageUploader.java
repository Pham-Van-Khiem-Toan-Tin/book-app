package com.book.app.Config;

import com.book.app.DTO.CloudinaryForm;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ImageUploader {
    private Cloudinary cloudinary;

    public ImageUploader() {
        // Set your Cloudinary credentials
        Dotenv dotenv = Dotenv.load();
        System.out.println(dotenv.get("CLOUDINARY_URL"));
        cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        cloudinary.config.secure = true;
        System.out.println(cloudinary.config.cloudName);
    }

    public CloudinaryForm uploadImage(File file, String folder) throws IOException {
// Upload the image
        Map params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "unique_filename", true,
                "overwrite", true
        );
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file, params);
        CloudinaryForm result = new CloudinaryForm();
        result.setUrl((String) uploadResult.get("url"));
        result.setPublic_id((String) uploadResult.get("public_id"));
        return result;
    }

    public String destroyImage(String public_id) throws Exception {
        Map params = ObjectUtils.asMap(
                "type", "upload",
                "resource_type", "image"
        );

        Map<?, ?> destroyResult = cloudinary.uploader().destroy(public_id, params);
        if (destroyResult.get("result").equals("ok")) {

            return "deleted!";
        }
        return "delete error";
    }
}
