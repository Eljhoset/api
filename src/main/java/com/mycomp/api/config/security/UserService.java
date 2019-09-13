package com.mycomp.api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Daniel
 */
public abstract class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User login(String username, String password) {
        User user = loadUserByUsername(username);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public abstract User loadUserByUsername(String username) throws UsernameNotFoundException;
}
