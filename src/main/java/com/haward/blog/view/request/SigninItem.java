package com.haward.blog.view.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 *  Login info
 */
@Data
public class SigninItem {

    @NotBlank
    @Size(min=5, max=70)
    private String username;

    @NotBlank
    @Size(min=5, max=70)
    private String password;
}
