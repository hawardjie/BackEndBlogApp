package com.haward.blog.view.request;

import lombok.Data;

import java.time.Instant;

/**
 * User's comment
 */
@Data
public class CommentItem {
    private Long id;
    private String content;
    private String username;
    private Instant createdOn;
    private Long postId;
}
