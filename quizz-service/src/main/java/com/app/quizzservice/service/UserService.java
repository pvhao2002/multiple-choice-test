package com.app.quizzservice.service;

import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.model.enums.GenderEnum;
import com.app.quizzservice.model.enums.StatusEnum;
import com.app.quizzservice.repo.UserRepo;
import com.app.quizzservice.request.dto.UserV2DTO;
import com.app.quizzservice.request.payload.CreateStudentPayload;
import com.app.quizzservice.request.payload.ImportExcelStudent;
import com.app.quizzservice.security.PasswordEncodeImpl;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.PagingUtil;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log
@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncodeImpl passwordEncode;

    public UserService(UserRepo userRepo, PasswordEncodeImpl passwordEncode) {
        this.userRepo = userRepo;
        this.passwordEncode = passwordEncode;
    }

    public List<UserV2DTO> findAllV2() {
        return userRepo.findAllV2();
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

    public boolean checkStudentIdExist(String studentId) {
        return userRepo.checkExistStudentId(studentId);
    }

    @Transactional
    public void createStudent(CreateStudentPayload payload, String avatar) {
        var user = User.builder()
                       .status(StatusEnum.ACTIVE)
                       .firstName(payload.firstName())
                       .lastName(payload.lastName())
                       .studentId(payload.studentId())
                       .gender(GenderEnum.valueOf(payload.gender().toUpperCase()))
                       .avatar(avatar)
                       .email(payload.studentId() + Constants.DEFAULT_TAIL_EMAIL)
                       .password(passwordEncode.encode(payload.studentId()))
                       .build();
        userRepo.save(user, true);
    }

    @Transactional
    public List<String> importStudentExcel(ArrayList<ImportExcelStudent> list) {
        var buildParam = list.stream()
                             .map(item -> item.toMap().addValue("password", passwordEncode.encode(item.password())))
                             .toList()
                             .toArray(new MapSqlParameterSource[0]);
        return userRepo.importExcel(buildParam);
    }

    public boolean checkSameToken(String token, long userId) {
        return userRepo.checkSameToken(token, userId);
    }
}
