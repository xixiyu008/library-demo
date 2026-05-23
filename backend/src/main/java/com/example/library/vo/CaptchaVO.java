package com.example.library.vo;

public class CaptchaVO {
    private String captchaKey;
    private String captchaCode;

    public CaptchaVO() {
    }

    public CaptchaVO(String captchaKey, String captchaCode) {
        this.captchaKey = captchaKey;
        this.captchaCode = captchaCode;
    }

    public String getCaptchaKey() { return captchaKey; }
    public void setCaptchaKey(String captchaKey) { this.captchaKey = captchaKey; }
    public String getCaptchaCode() { return captchaCode; }
    public void setCaptchaCode(String captchaCode) { this.captchaCode = captchaCode; }
}
