package com.haward.blog.repository;

import com.haward.blog.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This class facilitates the CRUD operations for table 'image' in DB
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
