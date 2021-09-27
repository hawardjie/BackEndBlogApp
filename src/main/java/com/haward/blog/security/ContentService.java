package com.haward.blog.security;

import com.haward.blog.model.Comment;
import com.haward.blog.model.Image;
import com.haward.blog.repository.CommentRepository;
import com.haward.blog.repository.ImageRepository;
import com.haward.blog.repository.PostRepository;
import com.haward.blog.model.Post;
import com.haward.blog.security.exception.ContentNotFoundException;
import com.haward.blog.security.exception.ImageNotFoundException;
import com.haward.blog.security.exception.ImageStoreException;
import com.haward.blog.security.userdetails.CustomUserDetails;
import com.haward.blog.view.request.CommentItem;
import com.haward.blog.view.request.PostItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ContentService class provides services for user content
 */
@Service
public class ContentService {

    @Autowired
    private AuthComponent authComponent; // to get current user

    @Autowired
    private PostRepository postRepository; // to save and find user's post

    @Autowired
    private CommentRepository commentRepository; // to save and find user's comment

    @Autowired
    private ImageRepository imageRepository; // to save and find an image

    /**
     * Persist user post and its image to database
     *
     * @param postItem New post item to be persisted
     * @param imageFile Image related to the post
     */
    @Transactional
    public void createPost(PostItem postItem, MultipartFile imageFile) {
        Image image = toImage(imageFile);
        imageRepository.save(image);
        Post post = toPost(postItem, image);
        postRepository.save(post);
    }

    /**
     * Persist user comment to database
     *
     * @param commentItem New user comment to be persisted
     */
    @Transactional
    public void createComment(CommentItem commentItem) {
        Comment comment = toComment(commentItem);
        commentRepository.save(comment);
    }

    /**
     * Retrieve a specific post
     *
     * @param id Post identification to be queried
     * @return Post item retrieved from the database
     */
    @Transactional
    public PostItem readPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Post (id=" + id + ") not found"));
        return toPostItem(post);
    }

    /**
     * Retrieve a specific comment
     *
     * @param id Comment identification to be queried
     * @return Comment item retrieved from the database
     */
    @Transactional
    public CommentItem readComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Comment (id=" + id + ") not found"));
        return toCommentItem(comment);
    }

    /**
     * Retrieve comments related to a specific post
     *
     * @param postId Post id to retrieve related comments
     * @return A list of comment items
     */
    @Transactional
    public List<CommentItem> readCommentsByPostId(Long postId) {
        List<Comment> commentList = commentRepository.findByPostId(postId);
        List<CommentItem> commentItemList = commentList.stream()
                        .map(comment -> toCommentItem(comment))
                        .collect(Collectors.toList());
        return commentItemList;
    }

    /**
     * Delete a specific comment
     *
     * @param id Id to be used to find target comment to delete
     */
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Comment (id=" + id + ") not found"));
        commentRepository.delete(comment);
    }

    /**
     * Get a list of the latest posted given its page number and number of requested posts.
     * A page has a batch of posts (i.e. 1st page has first n posts, 2nd page has next n posts)
     *
     * @param pageNumber Page number
     * @param size Number of requested posts
     * @return A list of the latest posts
     */
    @Transactional
    public List<PostItem> readPage(int pageNumber, int size) {
        // Get a number of posts sorted by their creation dates
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.Direction.ASC, "createdOn");
        Page<Post> page = postRepository.findAll(pageable);
        List<Post> topPosts = page.getContent();
        List<PostItem> postItemList = topPosts.stream()
                .map(post -> toPostItem(post))
                .collect((Collectors.toList()));
        return postItemList;
    }

    /**
     * Get image given its identification
     *
     * @param id Image unique identification
     * @return Image
     */
    @Transactional
    public Image getImage(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Image id " + id + " not found in repository"));
    }

    /**
     * Helper method to convert from Post model to PostItem
     *
     * @param post Post model
     * @return Post item
     */
    private PostItem toPostItem(Post post) {
        PostItem postItem = new PostItem();
        postItem.setId(post.getId());
        postItem.setTitle(post.getTitle());
        postItem.setContent(post.getContent());
        postItem.setUsername(post.getUsername());
        postItem.setCreatedOn(post.getCreatedOn());
        postItem.setImageId(post.getImageId());
        return postItem;
    }

    /**
     * Helper method to convert from Comment model to CommentItem
     *
     * @param comment Comment model
     * @return Comment item
     */
    private CommentItem toCommentItem(Comment comment) {
        CommentItem commentItem = new CommentItem();
        commentItem.setId(comment.getId());
        commentItem.setContent(comment.getContent());
        commentItem.setUsername(comment.getUsername());
        commentItem.setCreatedOn(comment.getCreatedOn());
        commentItem.setPostId(comment.getPostId());
        return commentItem;
    }

    /**
     * Helper method to convert from multipart image file to Image model
     *
     * @param imageFile Multipart image file to be converted
     * @return Image model
     */
    private Image toImage(MultipartFile imageFile) {
        Image image = new Image();
        String normalizedFileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
        try {
            if (normalizedFileName.contains("..")) {
                throw new ImageStoreException("Found an invalid path " + normalizedFileName);
            }
            image.setFileName(normalizedFileName);
            image.setFileType(imageFile.getContentType());
            image.setFileContent(imageFile.getBytes());
        } catch (IOException e) {
            throw new ImageStoreException("Failed to store image file " + normalizedFileName, e);
        }
        return image;
    }

    /**
     * Helper method to convert from PostItem to Post model
     *
     * @param postItem Post item
     * @param imageFile Image for the post
     * @return Post model
     */
    private Post toPost(PostItem postItem, Image imageFile) {
        Post post = new Post();
        post.setTitle(postItem.getTitle());
        post.setContent(postItem.getContent());
        post.setImageId(imageFile.getId());
        post.setCreatedOn(Instant.now());
        // Get current active user
        CustomUserDetails user = authComponent.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        post.setUsername(user.getUsername());

        return post;
    }

    /**
     * Helper method to convert from CommentItem to Comment model
     *
     * @param commentItem Comment item request
     * @return Comment model
     */
    private Comment toComment(CommentItem commentItem) {
        Comment comment = new Comment();
        comment.setContent(commentItem.getContent());
        comment.setCreatedOn(Instant.now());
        comment.setPostId(commentItem.getPostId());
        // Get current active user
        CustomUserDetails user = authComponent.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        comment.setUsername(user.getUsername());

        return comment;
    }
}
