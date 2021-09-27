package com.haward.blog.view.response;

import lombok.Data;

/**
 * Response message to support authentication endpoint (AuthEndPoint class)
 */
@Data
public class ResponseMessage {

    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }
}
