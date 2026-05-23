package com.example.library.service;

import com.example.library.common.PageResult;
import com.example.library.dto.BookCreateRequest;
import com.example.library.dto.BookUpdateRequest;
import com.example.library.vo.BookVO;

public interface BookService {
    PageResult<BookVO> page(int page, int pageSize, String keyword);

    BookVO create(BookCreateRequest request);

    BookVO update(Long id, BookUpdateRequest request);

    void delete(Long id);
}
