package com.app.quizzservice.request.response;

import com.app.quizzservice.request.dto.SubjectDTO;

import java.util.Date;

public record SubjectResponse(
        long subjectId,
        String name,
        String icon,
        int count,
        Date lastUpdate
) {
    public SubjectResponse(SubjectDTO dto) {
        this(
                dto.getSubjectId(),
                dto.getName(),
                dto.getIcon(),
                dto.getCount(),
                dto.getLastUpdate()
        );
    }
}
