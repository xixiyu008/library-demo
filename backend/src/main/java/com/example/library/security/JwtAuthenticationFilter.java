package com.example.library.security;

import com.example.library.entity.User;
import com.example.library.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenStore tokenStore, UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenStore = tokenStore;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                JwtClaims claims = jwtTokenProvider.parseToken(token);
                if (!tokenStore.isBlacklisted(token) && tokenStore.isCurrentLogin(claims.getUserId(), token)) {
                    User user = userMapper.selectById(claims.getUserId());
                    if (user != null && Boolean.TRUE.equals(user.getEnabled())) {
                        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), true);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
