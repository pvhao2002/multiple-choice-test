package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PagingUtil {
    public int calculateOffset(int page, int size) {
        return (getPageOrDefault(page) - 1) * getSizeOrDefault(size);
    }

    public int getPageOrDefault(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    public int getSizeOrDefault(Integer size) {
        return size == null || size < 1 ? 10 : size;
    }
}
