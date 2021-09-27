package com.haward.blog.endpoint;

import com.haward.blog.view.request.SigninItem;
import com.haward.blog.view.request.SignupItem;
import com.haward.blog.view.response.JwtResponse;
import com.haward.blog.view.response.ResponseMessage;
import com.haward.blog.model.User;
import com.haward.blog.repository.UserRepository;
import com.haward.blog.security.AuthComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *   This class has all the user endpoints to login and create a new account.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthEndPoint {

    @Autowired
    AuthenticationManager authenticationManager; // to authenticate user

    @Autowired
    UserRepository userRepository; // to retrieve and save user info

    @Autowired
    PasswordEncoder passwordEncoder;  // to encrypt user password before storing it to DB

    @Autowired
    AuthComponent authComponent; // to generate token

    /**
     * Authenticate user for login
     *
     * @param loginItem
     * @return Status 200 with user info if login info is valid. Else, return error status
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninItem loginItem) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginItem.getUsername(), loginItem.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = authComponent.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // find user in repository
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Uaer not found in repository"));
        // return status 200 with user info
        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername(),
                user.getFirstname(), user.getLastname()));
    }

    /**
     * Sign up a new user
     *
     * @param signUpItem New user info
     * @return HttpStatus.OK after user has been registered successfully. Else, return error status
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupItem signUpItem) {
        // make sure username is unique
        if (userRepository.existsByUsername(signUpItem.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Username is taken. Try another."), HttpStatus.BAD_REQUEST);
        }

        // make sure email is unique
        if (userRepository.existsByEmail(signUpItem.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Email is taken. Try another."), HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpItem.getFirstname(), signUpItem.getLastname(), signUpItem.getUsername(),
                signUpItem.getEmail(), passwordEncoder.encode(signUpItem.getPassword()));
        userRepository.save(user); // persist user info into DB

        return new ResponseEntity<>(new ResponseMessage(
                "User " + signUpItem.getFirstname() + " has been created successfully"),
                HttpStatus.OK);
    }

}
