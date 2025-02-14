package com.app.quizzservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public final class PagingContainer<T> {
    private int page;
    private int size;
    private long totalRecords;
    private List<T> contents;

    public PagingContainer<T> withPage(int page) {
        this.page = page;
        return this;
    }
}
