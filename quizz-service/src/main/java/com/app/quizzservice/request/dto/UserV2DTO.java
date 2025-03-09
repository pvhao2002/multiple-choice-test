package com.app.quizzservice.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserV2DTO {
    long userId;
    String email;
    String password;
    String firstName;
    String lastName;
    String studentId;
    String label;

    public UserV2DTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("student_id"),
                rs.getString("label")
        );
    }
}
