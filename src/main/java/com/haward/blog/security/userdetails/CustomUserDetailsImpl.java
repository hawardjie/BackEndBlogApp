package com.haward.blog.security.userdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haward.blog.model.User;
import com.haward.blog.repository.UserRepository;

/**
 * Custer user details implementation for user service
 */
@Service
public class CustomUserDetailsImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository; // bridge to query table 'user' in DB

    /**
     * Get user details given its username
     *
     * @param username Username input
     * @return User details
     * @throws UsernameNotFoundException Exception thrown for user retrieval failure
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = null;
        try {
            // query the DB to get user given its name
            user = (User) userRepository.findByUsername(username).orElseThrow(
                    () -> new UsernameNotFoundException("Username not found: " + username));
        } catch (Throwable throwable) {
            throw new UsernameNotFoundException("Failed to find username " + throwable.getMessage());
        }
        // Create instance of user details given the user info returns from DB
        return CustomUserDetails.build(user);
    }
}
