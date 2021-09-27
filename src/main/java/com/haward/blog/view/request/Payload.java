package com.haward.blog.view.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Payload of a new post
 */
@Data
public class Payload {
    private String title;
    private String content;
    private MultipartFile imageFile;
}
