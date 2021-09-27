package com.haward.blog.view.response;

import lombok.Data;

/**
 * JwtResponse to support user authentication in authentication endpoint (AuthEndPoint class)
 */
@Data
public class JwtResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String firstname;
    private String lastname;

    public JwtResponse(String accessToken, String username, String firstname, String lastname) {
        this.accessToken = accessToken;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
