package com.example.library.security;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenStore {
    private static final String LOGIN_KEY_PREFIX = "library:login:";
    private static final String BLACKLIST_KEY_PREFIX = "library:token:blacklist:";
    private static final String CAPTCHA_KEY_PREFIX = "library:captcha:";

    private final StringRedisTemplate redisTemplate;

    public TokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheLogin(Long userId, String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(LOGIN_KEY_PREFIX + userId, token, Duration.ofSeconds(ttlSeconds));
    }

    public boolean isCurrentLogin(Long userId, String token) {
        String currentToken = redisTemplate.opsForValue().get(LOGIN_KEY_PREFIX + userId);
        return token.equals(currentToken);
    }

    public void blacklist(String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + token, "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + token));
    }

    public void cacheCaptcha(String key, String code, Duration ttl) {
        redisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + key, code, ttl);
    }

    public boolean validateCaptcha(String key, String code) {
        String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + key);
        redisTemplate.delete(CAPTCHA_KEY_PREFIX + key);
        return cachedCode != null && cachedCode.equalsIgnoreCase(code);
    }
}
