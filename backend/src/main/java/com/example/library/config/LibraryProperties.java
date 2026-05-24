package com.example.library.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "library")
public class LibraryProperties {
    private final Jwt jwt = new Jwt();
    private final Captcha captcha = new Captcha();
    private final Borrow borrow = new Borrow();
    private final Page page = new Page();

    public Jwt getJwt() { return jwt; }
    public Captcha getCaptcha() { return captcha; }
    public Borrow getBorrow() { return borrow; }
    public Page getPage() { return page; }

    public static class Jwt {
        @NotBlank
        private String secret;
        @Min(1)
        private long expireDays = 7;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpireDays() { return expireDays; }
        public void setExpireDays(long expireDays) { this.expireDays = expireDays; }
    }

    public static class Captcha {
        private boolean enabled = false;
        @Min(1)
        private long ttlMinutes = 5;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getTtlMinutes() { return ttlMinutes; }
        public void setTtlMinutes(long ttlMinutes) { this.ttlMinutes = ttlMinutes; }
    }

    public static class Borrow {
        @Min(1)
        private int maxActiveBorrows = 5;
        @Min(1)
        private int days = 30;

        public int getMaxActiveBorrows() { return maxActiveBorrows; }
        public void setMaxActiveBorrows(int maxActiveBorrows) { this.maxActiveBorrows = maxActiveBorrows; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
    }

    public static class Page {
        @Min(1)
        private int defaultSize = 10;
        @Min(1)
        @Max(200)
        private int maxSize = 50;

        public int getDefaultSize() { return defaultSize; }
        public void setDefaultSize(int defaultSize) { this.defaultSize = defaultSize; }
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
    }
}
