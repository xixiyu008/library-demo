package com.example.library.controller;

import com.example.library.common.PageResult;
import com.example.library.common.Result;
import com.example.library.dto.ReaderCreateRequest;
import com.example.library.dto.ReaderUpdateRequest;
import com.example.library.service.ReaderService;
import com.example.library.vo.ReaderVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class ReaderController {
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public Result<PageResult<ReaderVO>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String keyword) {
        return Result.success(readerService.page(page, pageSize, keyword));
    }

    @PostMapping
    public Result<ReaderVO> create(@Valid @RequestBody ReaderCreateRequest request) {
        return Result.success(readerService.create(request));
    }

    @PutMapping("/{id}")
    public Result<ReaderVO> update(@PathVariable Long id, @Valid @RequestBody ReaderUpdateRequest request) {
        return Result.success(readerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        readerService.delete(id);
        return Result.success();
    }
}
