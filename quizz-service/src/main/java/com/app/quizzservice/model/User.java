package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.GenderEnum;
import com.app.quizzservice.model.enums.RoleEnum;
import com.app.quizzservice.model.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    long userId;
    String email;
    String password;
    String firstName;
    String lastName;

    GenderEnum gender;
    String avatar;
    RoleEnum role;
    StatusEnum status;

    public User(ResultSet rs) throws SQLException {
        this(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                GenderEnum.valueOf(rs.getString("gender")),
                rs.getString("avatar"),
                RoleEnum.valueOf(rs.getString("role")),
                StatusEnum.valueOf(rs.getString("status"))
        );
    }
}
