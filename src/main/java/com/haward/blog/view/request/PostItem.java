package com.haward.blog.view.request;

import lombok.Data;

import java.time.Instant;

/**
 * User's post item
 */
@Data
public class PostItem {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Instant createdOn;
    private Long imageId;
}
