package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.library.common.ErrorCode;
import com.example.library.common.PageResult;
import com.example.library.dto.BookCreateRequest;
import com.example.library.dto.BookUpdateRequest;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.enums.BookStatus;
import com.example.library.enums.BorrowStatus;
import com.example.library.exception.BusinessException;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.service.BookService;
import com.example.library.vo.BookVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public BookServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    public PageResult<BookVO> page(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Book::getTitle, keyword).or().like(Book::getIsbn, keyword).or().like(Book::getAuthor, keyword);
        }
        wrapper.orderByDesc(Book::getCreateTime);
        Page<Book> result = bookMapper.selectPage(Page.of(page, pageSize), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public BookVO create(BookCreateRequest request) {
        validateCounts(request.getTotalCount(), request.getAvailableCount());
        LocalDateTime now = LocalDateTime.now();
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setTotalCount(request.getTotalCount());
        book.setAvailableCount(request.getAvailableCount());
        book.setStatus(BookStatus.AVAILABLE);
        book.setCreateTime(now);
        book.setUpdateTime(now);
        bookMapper.insert(book);
        return toVO(book);
    }

    @Override
    public BookVO update(Long id, BookUpdateRequest request) {
        validateCounts(request.getTotalCount(), request.getAvailableCount());
        Book book = requireBook(id);
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setTotalCount(request.getTotalCount());
        book.setAvailableCount(request.getAvailableCount());
        book.setStatus(request.getStatus());
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
        return toVO(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireBook(id);
        Long activeBorrowCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getBookId, id)
                .in(BorrowRecord::getStatus, BorrowStatus.BORROWED, BorrowStatus.OVERDUE));
        if (activeBorrowCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "存在未归还借阅记录，不能删除图书");
        }
        bookMapper.deleteById(id);
    }

    private void validateCounts(Integer totalCount, Integer availableCount) {
        if (availableCount > totalCount) {
            throw new BusinessException(ErrorCode.CONFLICT, "可借数量不能大于总数量");
        }
    }

    private Book requireBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "图书不存在");
        }
        return book;
    }

    private BookVO toVO(Book book) {
        BookVO vo = new BookVO();
        vo.setId(book.getId());
        vo.setIsbn(book.getIsbn());
        vo.setTitle(book.getTitle());
        vo.setAuthor(book.getAuthor());
        vo.setPublisher(book.getPublisher());
        vo.setCategory(book.getCategory());
        vo.setTotalCount(book.getTotalCount());
        vo.setAvailableCount(book.getAvailableCount());
        vo.setStatus(book.getStatus());
        return vo;
    }
}
