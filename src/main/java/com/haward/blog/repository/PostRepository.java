package com.haward.blog.repository;

import com.haward.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This class facilitates the CRUD operations for table 'post' in DB
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
