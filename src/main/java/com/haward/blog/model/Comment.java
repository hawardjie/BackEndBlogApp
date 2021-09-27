package com.haward.blog.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

/*
    Comment is the content feedback of a specific post.
    A comment must have a reference to a specific post.
    A post can have 0 or more comment from the same or various users.
 */

@Data
@Entity
@Table
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column
    @NotEmpty
    private String content;

    @Column
    private Instant createdOn;

    @Column
    @NotBlank
    private String username;   // comment creator's username

    @Column
    @NotBlank
    private Long postId;   // reference to a specific post to comment
}
