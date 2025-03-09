package com.app.quizzservice.request.payload;

import com.app.quizzservice.utils.Constants;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.text.MessageFormat;
import java.util.Arrays;

public record ImportExcelStudent(
        String email,
        String studentId,
        String password,
        String firstName,
        String lastName,
        String fullName,
        String gender
) {
    public ImportExcelStudent(Row row) {
        this(
                MessageFormat.format("{0}{1}", row.getCell(0).getStringCellValue(), Constants.DEFAULT_TAIL_EMAIL),
                row.getCell(0).getStringCellValue(),
                row.getCell(0).getStringCellValue(),
                Arrays.stream(row.getCell(1).getStringCellValue().split(Constants.SPACE))
                      .findFirst()
                      .map(String::trim)
                      .orElse(""),  // first name
                Arrays.stream(row.getCell(1).getStringCellValue().split(Constants.SPACE))
                      .skip(1)
                      .reduce((s1, s2) -> s1 + Constants.SPACE + s2)
                      .map(String::trim)
                      .orElse(""),  // last name
                row.getCell(1).getStringCellValue(),
                row.getCell(2).getStringCellValue()
        );
    }

    public MapSqlParameterSource toMap() {
        return new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("studentId", studentId)
                .addValue("firstName", firstName)
                .addValue("lastName", lastName)
                .addValue("fullName", fullName)
                .addValue("gender", gender().equalsIgnoreCase("nữ") ? "female" : "male");
    }
}
