package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.library.common.ErrorCode;
import com.example.library.common.PageResult;
import com.example.library.common.PageSupport;
import com.example.library.config.LibraryProperties;
import com.example.library.dto.ReaderCreateRequest;
import com.example.library.dto.ReaderUpdateRequest;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Reader;
import com.example.library.entity.User;
import com.example.library.enums.BorrowStatus;
import com.example.library.enums.ReaderStatus;
import com.example.library.enums.UserRole;
import com.example.library.exception.BusinessException;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.mapper.ReaderMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.service.ReaderService;
import com.example.library.vo.ReaderVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ReaderServiceImpl implements ReaderService {
    private final ReaderMapper readerMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final UserMapper userMapper;
    private final LibraryProperties properties;

    public ReaderServiceImpl(ReaderMapper readerMapper, BorrowRecordMapper borrowRecordMapper, UserMapper userMapper,
                             LibraryProperties properties) {
        this.readerMapper = readerMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.userMapper = userMapper;
        this.properties = properties;
    }

    @Override
    public PageResult<ReaderVO> page(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Reader::getName, keyword).or().like(Reader::getStudentNo, keyword);
        }
        wrapper.orderByDesc(Reader::getCreateTime);
        Page<Reader> result = readerMapper.selectPage(Page.of(
                PageSupport.normalizePage(page),
                PageSupport.normalizePageSize(pageSize, properties)), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public ReaderVO create(ReaderCreateRequest request) {
        if (existsStudentNo(request.getStudentNo(), null)) {
            throw new BusinessException(ErrorCode.CONFLICT, "学号已存在");
        }
        validateUserBinding(request.getUserId(), null);
        LocalDateTime now = LocalDateTime.now();
        Reader reader = new Reader();
        reader.setStudentNo(request.getStudentNo());
        reader.setName(request.getName());
        reader.setCollege(request.getCollege());
        reader.setPhone(request.getPhone());
        reader.setUserId(request.getUserId());
        reader.setStatus(ReaderStatus.ACTIVE);
        reader.setCreateTime(now);
        reader.setUpdateTime(now);
        readerMapper.insert(reader);
        return toVO(reader);
    }

    @Override
    public ReaderVO update(Long id, ReaderUpdateRequest request) {
        Reader reader = requireReader(id);
        if (existsStudentNo(request.getStudentNo(), id)) {
            throw new BusinessException(ErrorCode.CONFLICT, "学号已存在");
        }
        validateUserBinding(request.getUserId(), id);
        reader.setStudentNo(request.getStudentNo());
        reader.setName(request.getName());
        reader.setCollege(request.getCollege());
        reader.setPhone(request.getPhone());
        reader.setUserId(request.getUserId());
        reader.setStatus(request.getStatus());
        reader.setUpdateTime(LocalDateTime.now());
        readerMapper.updateById(reader);
        return toVO(reader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireReader(id);
        Long activeBorrowCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, id)
                .in(BorrowRecord::getStatus, BorrowStatus.BORROWED, BorrowStatus.OVERDUE));
        if (activeBorrowCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "存在未归还记录，不能删除读者");
        }
        readerMapper.deleteById(id);
    }

    private boolean existsStudentNo(String studentNo, Long excludeId) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<Reader>().eq(Reader::getStudentNo, studentNo);
        if (excludeId != null) {
            wrapper.ne(Reader::getId, excludeId);
        }
        return readerMapper.selectCount(wrapper) > 0;
    }

    private void validateUserBinding(Long userId, Long readerId) {
        if (userId == null) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关联用户不存在");
        }
        if (user.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ErrorCode.CONFLICT, "读者只能关联学生用户");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(ErrorCode.CONFLICT, "不能关联已停用用户");
        }
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<Reader>().eq(Reader::getUserId, userId);
        if (readerId != null) {
            wrapper.ne(Reader::getId, readerId);
        }
        if (readerMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该用户已关联其他读者");
        }
    }

    private Reader requireReader(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "读者不存在");
        }
        return reader;
    }

    private ReaderVO toVO(Reader reader) {
        ReaderVO vo = new ReaderVO();
        vo.setId(reader.getId());
        vo.setStudentNo(reader.getStudentNo());
        vo.setName(reader.getName());
        vo.setCollege(reader.getCollege());
        vo.setPhone(reader.getPhone());
        vo.setStatus(reader.getStatus());
        vo.setUserId(reader.getUserId());
        return vo;
    }
}
