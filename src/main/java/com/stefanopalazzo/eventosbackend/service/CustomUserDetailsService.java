package com.stefanopalazzo.eventosbackend.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Por ahora cualquier usuario sirve con password "1234"
        return new User(username, "{noop}1234", Collections.emptyList());
    }
}
