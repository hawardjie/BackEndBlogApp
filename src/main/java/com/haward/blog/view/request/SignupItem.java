package com.haward.blog.view.request;

import lombok.Data;

import javax.validation.constraints.*;

/**
 *  New user info for registration
 */
@Data
public class SignupItem {

    @NotBlank
    @Size(min=5, max=70)
    private String firstname;

    @NotBlank
    @Size(min=5, max=70)
    private String lastname;

    @NotBlank
    @Size(min=5, max=70)
    private String username;

    @NotBlank
    @Size(max=100)
    @Email
    private String email;

    @NotBlank
    @Size(min=8, max=80)
    private String password;
}
