package com.haward.blog.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/*
    This class represents a user model.
    It has the user details captured during signup/registration.
    The info is used to authenticate user and to retrieve back info
    after user has logged in successfully.
 */

@Data
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "username" }),
        @UniqueConstraint(columnNames = { "email" })
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @NaturalId
    private String email;

    @NotBlank
    @Size(min=8, max=80)
    private String password;

    protected User() {}

    public User(String firstname, String lastname, String username, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
