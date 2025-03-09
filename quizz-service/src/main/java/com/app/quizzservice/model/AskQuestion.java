package com.app.quizzservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AskQuestion {
    long id;
    String studentId;
    String question;
    String answer;
    Date createdAt;
}
