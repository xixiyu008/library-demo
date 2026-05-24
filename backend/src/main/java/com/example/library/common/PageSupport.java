package com.example.library.common;

import com.example.library.config.LibraryProperties;

public final class PageSupport {
    private PageSupport() {
    }

    public static int normalizePage(int page) {
        return Math.max(page, 1);
    }

    public static int normalizePageSize(int pageSize, LibraryProperties properties) {
        int fallback = properties.getPage().getDefaultSize();
        int requested = pageSize <= 0 ? fallback : pageSize;
        return Math.min(requested, properties.getPage().getMaxSize());
    }
}
