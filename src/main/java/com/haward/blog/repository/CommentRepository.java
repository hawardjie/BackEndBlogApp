package com.haward.blog.repository;

import com.haward.blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This class facilitates the CRUD operations for table 'comment' in DB
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
