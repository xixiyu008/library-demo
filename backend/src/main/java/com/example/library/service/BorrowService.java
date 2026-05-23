package com.example.library.service;

import com.example.library.common.PageResult;
import com.example.library.dto.BorrowRequest;
import com.example.library.enums.BorrowStatus;
import com.example.library.security.LoginUser;
import com.example.library.vo.BorrowRecordVO;
import com.example.library.vo.BorrowResponse;

public interface BorrowService {
    BorrowResponse borrow(BorrowRequest request);

    void returnBook(Long id);

    PageResult<BorrowRecordVO> page(int page, int pageSize, Long readerId, Long bookId, BorrowStatus status);

    PageResult<BorrowRecordVO> my(int page, int pageSize, LoginUser loginUser);
}
