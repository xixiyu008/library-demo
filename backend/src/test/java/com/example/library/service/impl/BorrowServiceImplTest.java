package com.example.library.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.library.config.LibraryProperties;
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
import com.example.library.vo.BorrowResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BorrowServiceImplTest {
    @Mock
    private BorrowRecordMapper borrowRecordMapper;
    @Mock
    private ReaderMapper readerMapper;
    @Mock
    private BookMapper bookMapper;

    private BorrowServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LibraryProperties properties = new LibraryProperties();
        properties.getBorrow().setDays(14);
        properties.getBorrow().setMaxActiveBorrows(5);
        properties.getPage().setDefaultSize(10);
        properties.getPage().setMaxSize(50);
        service = new BorrowServiceImpl(borrowRecordMapper, readerMapper, bookMapper, properties);
    }

    @Test
    void borrowCreatesRecordWithConfiguredDueDays() {
        Reader reader = activeReader();
        Book book = availableBook(2);
        when(readerMapper.selectByIdForUpdate(1L)).thenReturn(reader);
        when(bookMapper.selectByIdForUpdate(2L)).thenReturn(book);
        when(borrowRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(bookMapper.decrementAvailable(eq(2L), any())).thenReturn(1);

        BorrowResponse response = service.borrow(request());

        ArgumentCaptor<BorrowRecord> recordCaptor = ArgumentCaptor.forClass(BorrowRecord.class);
        verify(borrowRecordMapper).insert(recordCaptor.capture());
        BorrowRecord record = recordCaptor.getValue();
        assertThat(record.getReaderId()).isEqualTo(1L);
        assertThat(record.getBookId()).isEqualTo(2L);
        assertThat(record.getStatus()).isEqualTo(BorrowStatus.BORROWED);
        assertThat(record.getDueTime()).isEqualTo(record.getBorrowTime().plusDays(14));
        assertThat(response).isNotNull();
    }

    @Test
    void borrowDoesNotCreateRecordWhenAtomicStockUpdateFails() {
        when(readerMapper.selectByIdForUpdate(1L)).thenReturn(activeReader());
        when(bookMapper.selectByIdForUpdate(2L)).thenReturn(availableBook(1));
        when(borrowRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(bookMapper.decrementAvailable(eq(2L), any())).thenReturn(0);

        assertThatThrownBy(() -> service.borrow(request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("库存不足或图书不可借");
        verify(borrowRecordMapper, never()).insert(any(BorrowRecord.class));
    }

    @Test
    void returnBookMarksRecordReturnedAndIncrementsStock() {
        BorrowRecord record = new BorrowRecord();
        record.setId(10L);
        record.setReaderId(1L);
        record.setBookId(2L);
        record.setStatus(BorrowStatus.BORROWED);
        when(borrowRecordMapper.selectByIdForUpdate(10L)).thenReturn(record);
        when(bookMapper.selectByIdForUpdate(2L)).thenReturn(availableBook(1));
        when(bookMapper.incrementAvailable(eq(2L), any())).thenReturn(1);

        service.returnBook(10L);

        assertThat(record.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(record.getReturnTime()).isNotNull();
        verify(borrowRecordMapper).updateById(record);
        verify(bookMapper).incrementAvailable(eq(2L), any());
    }

    private BorrowRequest request() {
        BorrowRequest request = new BorrowRequest();
        request.setReaderId(1L);
        request.setBookId(2L);
        return request;
    }

    private Reader activeReader() {
        Reader reader = new Reader();
        reader.setId(1L);
        reader.setStatus(ReaderStatus.ACTIVE);
        return reader;
    }

    private Book availableBook(int availableCount) {
        Book book = new Book();
        book.setId(2L);
        book.setStatus(BookStatus.AVAILABLE);
        book.setAvailableCount(availableCount);
        book.setTotalCount(2);
        return book;
    }
}
