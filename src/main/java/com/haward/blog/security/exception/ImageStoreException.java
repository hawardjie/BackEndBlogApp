package com.haward.blog.security.exception;
/**
 * Exception class for image persistent APIs
 */
public class ImageStoreException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImageStoreException(String message) {
        super(message);
    }

    public ImageStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
