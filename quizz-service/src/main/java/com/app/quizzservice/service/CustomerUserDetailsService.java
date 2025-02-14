package com.app.quizzservice.service;

import com.app.quizzservice.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomerUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo
                .findByEmailV2(email)
                .orElseThrow(() -> new UsernameNotFoundException("NOT FOUND EMAIL!"));
    }
}
