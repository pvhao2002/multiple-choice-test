package com.app.quizzservice.request.payload;

public record AddStudentPayload(
        long studentId,
        String email,
        String firstName,
        String lastName,
        String gender
) {
}
