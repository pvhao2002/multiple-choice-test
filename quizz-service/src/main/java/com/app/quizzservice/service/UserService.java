package com.app.quizzservice.service;

import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.model.enums.StatusEnum;
import com.app.quizzservice.repo.UserRepo;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.PagingUtil;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public PagingContainer<User> findAll(Integer page, Integer limit, String key) {
        var offset = PagingUtil.calculateOffset(page, limit);
        return userRepo.findAllWithPaging(offset, limit, key)
                       .withPage(page);
    }

    public String updateStatus(long userId, boolean status) {
        var statusStr = status ? StatusEnum.ACTIVE.name() : StatusEnum.BLOCKED.name();
        userRepo.changeStatus(userId, statusStr);
        return Constants.SUCCESS;
    }

}
