package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.library.common.ErrorCode;
import com.example.library.common.PageResult;
import com.example.library.dto.BorrowRequest;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Reader;
import com.example.library.enums.BookStatus;
import com.example.library.enums.BorrowStatus;
import com.example.library.enums.ReaderStatus;
import com.example.library.exception.BusinessException;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.mapper.ReaderMapper;
import com.example.library.security.LoginUser;
import com.example.library.service.BorrowService;
import com.example.library.vo.BorrowRecordVO;
import com.example.library.vo.BorrowResponse;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BorrowServiceImpl implements BorrowService {
    private static final int MAX_ACTIVE_BORROWS = 5;
    private static final int BORROW_DAYS = 30;

    private final BorrowRecordMapper borrowRecordMapper;
    private final ReaderMapper readerMapper;
    private final BookMapper bookMapper;

    public BorrowServiceImpl(BorrowRecordMapper borrowRecordMapper, ReaderMapper readerMapper, BookMapper bookMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.readerMapper = readerMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowResponse borrow(BorrowRequest request) {
        Reader reader = requireReader(request.getReaderId());
        Book book = requireBook(request.getBookId());
        validateBorrow(reader, book);

        LocalDateTime now = LocalDateTime.now();
        BorrowRecord record = new BorrowRecord();
        record.setReaderId(reader.getId());
        record.setBookId(book.getId());
        record.setBorrowTime(now);
        record.setDueTime(now.plusDays(BORROW_DAYS));
        record.setStatus(BorrowStatus.BORROWED);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        borrowRecordMapper.insert(record);

        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setUpdateTime(now);
        bookMapper.updateById(book);
        return new BorrowResponse(record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借阅记录不存在");
        }
        if (record.getStatus() == BorrowStatus.RETURNED || record.getReturnTime() != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "该记录已经归还");
        }
        Book book = requireBook(record.getBookId());
        LocalDateTime now = LocalDateTime.now();
        record.setReturnTime(now);
        record.setStatus(BorrowStatus.RETURNED);
        record.setUpdateTime(now);
        borrowRecordMapper.updateById(record);

        book.setAvailableCount(book.getAvailableCount() + 1);
        book.setUpdateTime(now);
        bookMapper.updateById(book);
    }

    @Override
    public PageResult<BorrowRecordVO> page(int page, int pageSize, Long readerId, Long bookId, BorrowStatus status) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(readerId != null, BorrowRecord::getReaderId, readerId)
                .eq(bookId != null, BorrowRecord::getBookId, bookId)
                .eq(status != null, BorrowRecord::getStatus, status)
                .orderByDesc(BorrowRecord::getBorrowTime);
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(Page.of(page, pageSize), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public PageResult<BorrowRecordVO> my(int page, int pageSize, LoginUser loginUser) {
        Reader reader = readerMapper.selectOne(new LambdaQueryWrapper<Reader>().eq(Reader::getUserId, loginUser.getId()));
        if (reader == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户未关联读者");
        }
        return page(page, pageSize, reader.getId(), null, null);
    }

    private void validateBorrow(Reader reader, Book book) {
        if (reader.getStatus() != ReaderStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CONFLICT, "停用读者不能借书");
        }
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.CONFLICT, "停用图书不能借出");
        }
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "库存不足");
        }
        Long activeCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, reader.getId())
                .in(BorrowRecord::getStatus, BorrowStatus.BORROWED, BorrowStatus.OVERDUE));
        if (activeCount >= MAX_ACTIVE_BORROWS) {
            throw new BusinessException(ErrorCode.CONFLICT, "超过最大借阅数量");
        }
        Long overdueCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, reader.getId())
                .isNull(BorrowRecord::getReturnTime)
                .lt(BorrowRecord::getDueTime, LocalDateTime.now()));
        if (overdueCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "存在逾期未还记录，不能继续借书");
        }
        Long duplicateCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, reader.getId())
                .eq(BorrowRecord::getBookId, book.getId())
                .in(BorrowRecord::getStatus, BorrowStatus.BORROWED, BorrowStatus.OVERDUE));
        if (duplicateCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "不能重复借阅同一本未归还图书");
        }
    }

    private Reader requireReader(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "读者不存在");
        }
        return reader;
    }

    private Book requireBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "图书不存在");
        }
        return book;
    }

    private BorrowRecordVO toVO(BorrowRecord record) {
        Reader reader = readerMapper.selectById(record.getReaderId());
        Book book = bookMapper.selectById(record.getBookId());
        BorrowRecordVO vo = new BorrowRecordVO();
        vo.setId(record.getId());
        vo.setReaderId(record.getReaderId());
        vo.setBookId(record.getBookId());
        vo.setReaderName(reader == null ? null : reader.getName());
        vo.setBookTitle(book == null ? null : book.getTitle());
        vo.setBorrowTime(record.getBorrowTime());
        vo.setDueTime(record.getDueTime());
        vo.setReturnTime(record.getReturnTime());
        vo.setStatus(resolveStatus(record));
        return vo;
    }

    private BorrowStatus resolveStatus(BorrowRecord record) {
        if (record.getStatus() == BorrowStatus.RETURNED) {
            return BorrowStatus.RETURNED;
        }
        if (record.getDueTime().isBefore(LocalDateTime.now())) {
            return BorrowStatus.OVERDUE;
        }
        return record.getStatus();
    }
}
