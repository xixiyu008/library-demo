package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.library.common.ErrorCode;
import com.example.library.config.LibraryProperties;
import com.example.library.dto.LoginRequest;
import com.example.library.entity.User;
import com.example.library.exception.BusinessException;
import com.example.library.mapper.UserMapper;
import com.example.library.security.JwtTokenProvider;
import com.example.library.security.LoginUser;
import com.example.library.security.TokenStore;
import com.example.library.service.AuthService;
import com.example.library.vo.CaptchaVO;
import com.example.library.vo.CurrentUserVO;
import com.example.library.vo.LoginResponse;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String LOGIN_ERROR = "用户名或密码错误";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;
    private final LibraryProperties properties;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, TokenStore tokenStore, LibraryProperties properties) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenStore = tokenStore;
        this.properties = properties;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (properties.getCaptcha().isEnabled() || StringUtils.hasText(request.getCaptchaKey())) {
            if (!StringUtils.hasText(request.getCaptchaKey()) || !StringUtils.hasText(request.getCaptchaCode())
                    || !tokenStore.validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode())) {
                throw new BusinessException(ErrorCode.AUTH_FAILED, "验证码错误");
            }
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_FAILED, LOGIN_ERROR);
        }
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getRole());
        tokenStore.cacheLogin(user.getId(), token, jwtTokenProvider.secondsUntilExpiry(token));
        return new LoginResponse(token, user.getRole(), user.getUsername());
    }

    @Override
    public CurrentUserVO me(LoginUser loginUser) {
        return new CurrentUserVO(loginUser.getId(), loginUser.getUsername(), loginUser.getRole());
    }

    @Override
    public CaptchaVO captcha() {
        String key = UUID.randomUUID().toString();
        String code = String.valueOf(1000 + new Random().nextInt(9000));
        tokenStore.cacheCaptcha(key, code, Duration.ofMinutes(properties.getCaptcha().getTtlMinutes()));
        return new CaptchaVO(key, code);
    }

    @Override
    public void logout(LoginUser loginUser, String token) {
        tokenStore.blacklist(token, jwtTokenProvider.secondsUntilExpiry(token));
    }
}
