package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PagingUtil {
    private static final int DEFAULT_SIZE = 10;

    public int calculateOffset(int page, int size) {
        return (getPageOrDefault(page) - 1) * getSizeOrDefault(size);
    }

    public int getPageOrDefault(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    public int getSizeOrDefault(Integer size) {
        return size == null || size < 1 ? DEFAULT_SIZE : size;
    }

    public int getSizeOrDefault() {
        return DEFAULT_SIZE;
    }
}
