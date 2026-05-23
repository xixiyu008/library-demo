package com.example.library.service;

import com.example.library.common.PageResult;
import com.example.library.dto.ReaderCreateRequest;
import com.example.library.dto.ReaderUpdateRequest;
import com.example.library.vo.ReaderVO;

public interface ReaderService {
    PageResult<ReaderVO> page(int page, int pageSize, String keyword);

    ReaderVO create(ReaderCreateRequest request);

    ReaderVO update(Long id, ReaderUpdateRequest request);

    void delete(Long id);
}
