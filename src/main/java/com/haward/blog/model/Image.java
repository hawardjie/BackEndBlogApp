package com.haward.blog.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/*
    An image is owned by the same user who posted his/her content.
    An image is related (must have a reference) to a specific post (one to one mapping).
 */

@Data
@Entity
@Table
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    private String fileName;

    @Column
    @NotBlank
    private String fileType;  // i.e. jpg, jpeg, png

    @Lob
    private byte[] fileContent;  // binary image
}
