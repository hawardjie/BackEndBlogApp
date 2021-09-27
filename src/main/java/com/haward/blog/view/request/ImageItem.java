package com.haward.blog.view.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image for a specific post
 */
@Data
public class ImageItem {
    private String fileName;
    private String fileType;
    private MultipartFile content;
}
