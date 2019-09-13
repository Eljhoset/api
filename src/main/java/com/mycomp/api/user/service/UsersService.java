package com.mycomp.api.user.service;

import com.mycomp.api.config.security.UserService;
import com.mycomp.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel
 */
@Service
public class UsersService extends UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .map(e -> new User(e.getUsername(), e.getPassword(), e.getAuthorities())).orElseThrow(() -> {
            return new UsernameNotFoundException(String.format("User[%s] Not Found", username));
        });
    }

}
