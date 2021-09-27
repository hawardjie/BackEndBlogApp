package com.haward.blog.security;

import com.haward.blog.security.handlers.FilterHandler;
import com.haward.blog.security.handlers.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class provides security configuration
 */
@Configurable
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; // service to call password encoder

    @Autowired
    private UnauthorizedHandler unauthorizedHandler; // handler for unauthorized user

    @Bean
    public FilterHandler tokenFilter() {
        return new FilterHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Method to facilitate custom configuration
     *
     * @param httpSecurity HttpSecurity to configure web based security for http requests
     * @throws Exception Throw an exception for configuration failures
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // requesters must come from https
                .requiresChannel().anyRequest().requiresSecure().and()
                .cors().and().csrf().disable()
                .authorizeRequests()
                // no need to authenticate if path has the following patterns
                .antMatchers("/login").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/content/post/**").permitAll()
                .antMatchers("/content/page/**").permitAll()
                .antMatchers("/content/image/**").permitAll()
                .antMatchers("/content/comments/**").permitAll()
                .antMatchers("/content/comment/**").permitAll()
                // except the above path, we must authenticate all other incoming requests
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                // use stateless session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Filter each user request with the given token
        httpSecurity.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
