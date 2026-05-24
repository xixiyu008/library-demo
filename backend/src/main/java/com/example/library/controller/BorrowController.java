package com.example.library.controller;

import com.example.library.common.PageResult;
import com.example.library.common.Result;
import com.example.library.dto.BorrowRequest;
import com.example.library.enums.BorrowStatus;
import com.example.library.security.LoginUser;
import com.example.library.service.BorrowService;
import com.example.library.vo.BorrowRecordVO;
import com.example.library.vo.BorrowResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrows")
@Validated
public class BorrowController {
    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Result<BorrowResponse> borrow(@Valid @RequestBody BorrowRequest request) {
        return Result.success(borrowService.borrow(request));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Result<Void> returnBook(@PathVariable @Min(1) Long id) {
        borrowService.returnBook(id);
        return Result.success();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Result<PageResult<BorrowRecordVO>> page(@RequestParam(defaultValue = "1") @Min(1) int page,
                                                   @RequestParam(defaultValue = "10") @Min(1) int pageSize,
                                                   @RequestParam(required = false) Long readerId,
                                                   @RequestParam(required = false) Long bookId,
                                                   @RequestParam(required = false) BorrowStatus status) {
        return Result.success(borrowService.page(page, pageSize, readerId, bookId, status));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<BorrowRecordVO>> my(@RequestParam(defaultValue = "1") @Min(1) int page,
                                                 @RequestParam(defaultValue = "10") @Min(1) int pageSize,
                                                 @AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(borrowService.my(page, pageSize, loginUser));
    }
}
