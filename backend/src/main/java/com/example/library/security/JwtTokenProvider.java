package com.example.library.security;

import com.example.library.enums.UserRole;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    @Value("${library.jwt.secret}")
    private String secret;

    @Value("${library.jwt.expire-days}")
    private long expireDays;

    public String createToken(Long userId, String username, UserRole role) {
        Instant expiresAt = Instant.now().plus(Duration.ofDays(expireDays));
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"userId\":" + userId
                + ",\"username\":\"" + escape(username)
                + "\",\"role\":\"" + role.name()
                + "\",\"exp\":" + expiresAt.getEpochSecond() + "}");
        String unsignedToken = header + "." + payload;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public JwtClaims parseToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token");
        }
        String unsignedToken = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
            throw new IllegalArgumentException("Invalid signature");
        }
        String payload = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        Long userId = Long.valueOf(readJsonValue(payload, "userId"));
        String username = readJsonString(payload, "username");
        UserRole role = UserRole.valueOf(readJsonString(payload, "role"));
        Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(readJsonValue(payload, "exp")));
        if (expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expired token");
        }
        return new JwtClaims(userId, username, role, expiresAt);
    }

    public long secondsUntilExpiry(String token) {
        return Math.max(0, parseToken(token).getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond());
    }

    private String encode(String value) {
        return URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign token", exception);
        }
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigestSupport.equals(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private String readJsonString(String json, String field) {
        return readJsonValue(json, field).replace("\\\"", "\"");
    }

    private String readJsonValue(String json, String field) {
        String marker = "\"" + field + "\":";
        int start = json.indexOf(marker);
        if (start < 0) {
            throw new IllegalArgumentException("Missing claim");
        }
        start += marker.length();
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        }
        int end = json.indexOf(',', start);
        if (end < 0) {
            end = json.indexOf('}', start);
        }
        return json.substring(start, end);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
