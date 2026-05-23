package com.example.library.vo;

public class BorrowResponse {
    private Long borrowRecordId;

    public BorrowResponse() {
    }

    public BorrowResponse(Long borrowRecordId) {
        this.borrowRecordId = borrowRecordId;
    }

    public Long getBorrowRecordId() { return borrowRecordId; }
    public void setBorrowRecordId(Long borrowRecordId) { this.borrowRecordId = borrowRecordId; }
}
