package com.example.library.service;

import com.example.library.common.PageResult;
import com.example.library.dto.UserCreateRequest;
import com.example.library.dto.UserUpdateRequest;
import com.example.library.vo.UserVO;

public interface UserService {
    PageResult<UserVO> page(int page, int pageSize, String keyword);

    UserVO create(UserCreateRequest request);

    UserVO update(Long id, UserUpdateRequest request);

    void delete(Long id, Long currentUserId);
}
