package com.app.quizzservice.model;

import com.app.quizzservice.model.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Subject {
    long subjectId;
    String name;
    String icon;

    @Builder.Default
    StatusEnum status = StatusEnum.ACTIVE;

    public Subject(ResultSet rs) throws SQLException {
        this(
                rs.getLong("subject_id"),
                rs.getString("name"),
                rs.getString("icon"),
                StatusEnum.valueOf(rs.getString("status").toUpperCase())
        );
    }
}
