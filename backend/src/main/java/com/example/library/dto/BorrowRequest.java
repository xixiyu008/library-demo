package com.example.library.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowRequest {
    @NotNull
    private Long readerId;
    @NotNull
    private Long bookId;

    public Long getReaderId() { return readerId; }
    public void setReaderId(Long readerId) { this.readerId = readerId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
}
