package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.GenderEnum;
import com.app.quizzservice.model.enums.RoleEnum;
import com.app.quizzservice.model.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class User {
    long userId;
    String email;
    String password;
    String firstName;
    String lastName;
    String studentId;

    @Builder.Default
    GenderEnum gender = GenderEnum.OTHER;
    String avatar;
    @Builder.Default
    RoleEnum role = RoleEnum.STUDENT;
    @Builder.Default
    StatusEnum status = StatusEnum.INACTIVE;

    public User(long userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public User(ResultSet rs) throws SQLException {
        this(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("student_id"),
                GenderEnum.valueOf(rs.getString("gender").toUpperCase()),
                rs.getString("avatar"),
                RoleEnum.valueOf(rs.getString("role").toUpperCase()),
                StatusEnum.valueOf(rs.getString("status").toUpperCase())
        );
    }
}
