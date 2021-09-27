package com.haward.blog.endpoint;

import com.haward.blog.model.Image;
import com.haward.blog.security.ContentService;
import com.haward.blog.view.request.CommentItem;
import com.haward.blog.view.request.Payload;
import com.haward.blog.view.request.PostItem;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   This class has the endpoints to
 *   - create, read post
 *   - create, read, delete comment(s) of a specific post
 *   - retrieve an image belonged to a specific post
 */

@RestController
@RequestMapping("/content")
public class ContentEndPoint {

    @Autowired
    private ContentService contentService; // content manager for post, image, and comment

    /**
     * Create a new post
     * Precondition: User has been authenticated.
     *
     * @param payload New post info
     * #return HttpStatus.OK after persisting post content successfully. Else, return error status.
     */
    @RequestMapping(value="/new/post", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity createPost(@ModelAttribute Payload payload) {
        PostItem postItem = new PostItem();
        postItem.setTitle(payload.getTitle());
        postItem.setContent(payload.getContent());
        MultipartFile imageFile = payload.getImageFile();
        contentService.createPost(postItem, imageFile);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Create a new comment for a specific post
     * Precondition:
     * - User has been authenticated
     * - Comment for target post is existed
     *
     * @param commentItem New comment details from user
     * @return HttpStatus.OK after persisting comment successfully. Else, return error status.
     */
    @RequestMapping(value="/new/comment", method = RequestMethod.POST)
    public ResponseEntity createComment(@Valid @RequestBody CommentItem commentItem) {
        contentService.createComment(commentItem);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Retrieve a specific post
     *
     * @param id A unique identification of an existing post
     * @return Post info
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<PostItem> readPost(@PathVariable @RequestBody Long id) {
        return new ResponseEntity<>(contentService.readPost(id), HttpStatus.OK);
    }

    /**
     * Retrieve one of the comments of a specific post
     *
     * @param id Comment's unique identification
     * @return Comment info
     */
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentItem> readComment(@PathVariable @RequestBody Long id) {
        return new ResponseEntity<>(contentService.readComment(id), HttpStatus.OK);
    }

    /**
     * Delete a specific comment from database
     * Precondition: User has been authenticated.
     *
     * @param id An id to identify target comment to be deleted
     * @return Status OK after deleting comment successfully. Else, return error status
     */
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteComment(@PathVariable @RequestBody Long id) {
        contentService.deleteComment(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all the comments of a specific post
     *
     * @param id Post id
     * @return HttpStatus.OK along with the related comments of a specific post. Else, return error status
     */
    @GetMapping("/comments/post/{id}")
    public ResponseEntity<List<CommentItem>> readCommentsByPostId(@PathVariable @RequestBody Long id) {
        List<CommentItem> commentItemList = contentService.readCommentsByPostId(id);
        return new ResponseEntity<>(commentItemList, HttpStatus.OK);
    }

    /**
     * Retrieve max of the latest eleven posts
     *
     * @param id Page id
     * @return HttpStatus.OK along with the latest posts (11 posts at max). Else, return error status
     */
    @GetMapping("/page/{id}")
    public ResponseEntity<List<PostItem>> readPage(@PathVariable @RequestBody int id) {
        List<PostItem> postItemList = contentService.readPage(id, 11);
        return new ResponseEntity<>(postItemList, HttpStatus.OK);
    }

    /**
     * Retrieve the related image of a specific post
     *
     * @param id Post id
     * @return The related image with Status OK. Else, return error status
     */
    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable String id) {
        Image image = contentService.getImage(Long.parseLong(id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getFileContent()));
    }
}
