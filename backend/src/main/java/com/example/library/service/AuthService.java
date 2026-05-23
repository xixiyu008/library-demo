package com.example.library.service;

import com.example.library.dto.LoginRequest;
import com.example.library.security.LoginUser;
import com.example.library.vo.CaptchaVO;
import com.example.library.vo.CurrentUserVO;
import com.example.library.vo.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    CurrentUserVO me(LoginUser loginUser);

    CaptchaVO captcha();

    void logout(LoginUser loginUser, String token);
}
