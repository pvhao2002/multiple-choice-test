package com.app.quizzservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
    private long subjectId;
    private String name;
    private String icon;
    private int count;
    private int totalRow;
    private Date lastUpdate;

    public SubjectDTO(ResultSet rs) throws SQLException {
        this(
                rs.getLong("subject_id"),
                rs.getString("name"),
                rs.getString("icon"),
                rs.getInt("cnt"),
                rs.getInt("total_rows"),
                rs.getDate("last_update")
        );
    }
}
