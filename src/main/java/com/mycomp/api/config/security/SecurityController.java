package com.mycomp.api.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Daniel
 */
@RestController
public class SecurityController {

    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }
}
