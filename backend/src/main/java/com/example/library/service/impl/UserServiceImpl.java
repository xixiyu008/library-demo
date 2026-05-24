package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.library.common.ErrorCode;
import com.example.library.common.PageResult;
import com.example.library.common.PageSupport;
import com.example.library.config.LibraryProperties;
import com.example.library.dto.UserCreateRequest;
import com.example.library.dto.UserUpdateRequest;
import com.example.library.entity.Reader;
import com.example.library.entity.User;
import com.example.library.exception.BusinessException;
import com.example.library.mapper.ReaderMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.service.UserService;
import com.example.library.vo.UserVO;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final ReaderMapper readerMapper;
    private final PasswordEncoder passwordEncoder;
    private final LibraryProperties properties;

    public UserServiceImpl(UserMapper userMapper, ReaderMapper readerMapper, PasswordEncoder passwordEncoder,
                           LibraryProperties properties) {
        this.userMapper = userMapper;
        this.readerMapper = readerMapper;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public PageResult<UserVO> page(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(Page.of(
                PageSupport.normalizePage(page),
                PageSupport.normalizePageSize(pageSize, properties)), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public UserVO create(UserCreateRequest request) {
        if (existsUsername(request.getUsername(), null)) {
            throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在");
        }
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);
        return toVO(user);
    }

    @Override
    public UserVO update(Long id, UserUpdateRequest request) {
        User user = requireUser(id);
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return toVO(user);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        requireUser(id);
        if (id.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CONFLICT, "不能删除当前登录用户");
        }
        Long readerCount = readerMapper.selectCount(new LambdaQueryWrapper<Reader>().eq(Reader::getUserId, id));
        if (readerCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该用户已关联读者，不能删除");
        }
        userMapper.deleteById(id);
    }

    private boolean existsUsername(String username, Long excludeId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        return userMapper.selectCount(wrapper) > 0;
    }

    private User requireUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setEnabled(user.getEnabled());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
