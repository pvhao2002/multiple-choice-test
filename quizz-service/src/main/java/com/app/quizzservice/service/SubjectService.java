package com.app.quizzservice.service;

import com.app.quizzservice.model.PagingContainer;
import com.app.quizzservice.model.Subject;
import com.app.quizzservice.repo.SubjectRepo;
import com.app.quizzservice.request.response.SubjectResponse;
import com.app.quizzservice.utils.Base64Utils;
import com.app.quizzservice.utils.Constants;
import com.app.quizzservice.utils.PagingUtil;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log
@Service
public class SubjectService {
    private final SubjectRepo subjectRepo;

    public SubjectService(SubjectRepo subjectRepo) {
        this.subjectRepo = subjectRepo;
    }

    public String save(MultipartFile file, String name) {
        try {
            var image = Base64Utils.encodeImage(file);
            var subject = Subject.builder().name(name).icon(image).subjectId(0).build();
            subjectRepo.save(subject);
            return Constants.SUCCESS;
        } catch (IOException e) {
            log.severe(e.getMessage());
            return Constants.ERROR;
        }
    }

    public String save(Integer subId, String name, MultipartFile file) {
        try {
            String image = null;
            if (file != null && !file.isEmpty()) {
                image = Base64Utils.encodeImage(file);
            }
            var subject = Subject.builder().name(name).icon(image).subjectId(subId).build();
            subjectRepo.save(subject);
            return Constants.SUCCESS;
        } catch (IOException e) {
            log.severe(e.getMessage());
            return Constants.ERROR;
        }
    }

    public String delete(int id) {
        subjectRepo.delete(id);
        return Constants.SUCCESS;
    }

    public PagingContainer<Subject> getSubjects(int page, int size, String search) {
        var offset = PagingUtil.calculateOffset(page, size);
        return subjectRepo.getSubjects(offset, size, search).withPage(page);
    }

    public PagingContainer<SubjectResponse> getSubjectsV2(int page, int size) {
        var offset = PagingUtil.calculateOffset(page, size);
        return subjectRepo.getSubjectsV2(offset, size).withPage(page);
    }

}
