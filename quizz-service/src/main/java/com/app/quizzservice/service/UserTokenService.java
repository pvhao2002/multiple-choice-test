package com.app.quizzservice.service;

import com.app.quizzservice.model.UserToken;
import com.app.quizzservice.repo.UserTokenRepo;
import org.springframework.stereotype.Service;

@Service
public class UserTokenService {
    private final UserTokenRepo userTokenRepo;

    public UserTokenService(UserTokenRepo userTokenRepo) {
        this.userTokenRepo = userTokenRepo;
    }

    public void save(UserToken token) {
        userTokenRepo.save(token);
    }
}
