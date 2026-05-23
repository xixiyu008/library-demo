package com.example.library.controller;

import com.example.library.common.Result;
import com.example.library.dto.LoginRequest;
import com.example.library.security.LoginUser;
import com.example.library.service.AuthService;
import com.example.library.vo.CaptchaVO;
import com.example.library.vo.CurrentUserVO;
import com.example.library.vo.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/me")
    public Result<CurrentUserVO> me(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(authService.me(loginUser));
    }

    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        return Result.success(authService.captcha());
    }

    @PostMapping("/logout")
    public Result<Void> logout(@AuthenticationPrincipal LoginUser loginUser,
                               @RequestHeader("Authorization") String authorization) {
        authService.logout(loginUser, authorization.substring(7));
        return Result.success();
    }
}
