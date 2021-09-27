package com.haward.blog.security.userdetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

import com.haward.blog.model.User;

/**
 * Custom user details to support CustomUserDetailsImpl for user service
 */
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    public CustomUserDetails(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Create user details given its model info
     *
     * @param user User model
     * @return Instance of user details
     */
    public static CustomUserDetails build(User user) {
        return new CustomUserDetails(user.getId(), user.getUsername(), user.getEmail(),
                user.getPassword());
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }

    /*
     This method is not required for the blog
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomUserDetails userDetails = (CustomUserDetails) obj;
        return Objects.equals(id, userDetails.id);
    }
}
