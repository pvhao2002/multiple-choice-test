package com.app.quizzservice.request.payload;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public record ImportExcelQuetion(
        long examId,
        int no,
        String content,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String answer
) {
    public ImportExcelQuetion(Row row, long examId) {
        this(
                examId,
                (int) row.getCell(0).getNumericCellValue(),
                row.getCell(1).getStringCellValue(),
                row.getCell(2).getStringCellValue(),
                row.getCell(3).getStringCellValue(),
                row.getCell(4).getStringCellValue(),
                row.getCell(5).getStringCellValue(),
                row.getCell(6).getStringCellValue()
        );
    }

    public MapSqlParameterSource toMap() {
        return new MapSqlParameterSource()
                .addValue("examId", examId)
                .addValue("content", content)
                .addValue("optionA", optionA)
                .addValue("optionB", optionB)
                .addValue("optionC", optionC)
                .addValue("optionD", optionD)
                .addValue("answer", answer);
    }
}
