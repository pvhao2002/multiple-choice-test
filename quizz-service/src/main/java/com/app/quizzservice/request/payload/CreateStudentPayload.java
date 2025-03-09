package com.app.quizzservice.request.payload;

public record CreateStudentPayload(
        String studentId,
        String email,
        String firstName,
        String lastName,
        String gender
) {
}
