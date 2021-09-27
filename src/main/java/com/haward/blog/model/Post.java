package com.haward.blog.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

/*
    A post is the main blog in the app.
    A post has user content that gives details about a specific image.
 */

@Data
@Entity
@Table
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column
    private String title;

    @Lob
    @Column
    @NotEmpty
    private String content;

    @Column
    private Instant createdOn;

    @Column
    @NotBlank
    private String username; // creator's username

    @Column
    @NotBlank
    private Long imageId;
}