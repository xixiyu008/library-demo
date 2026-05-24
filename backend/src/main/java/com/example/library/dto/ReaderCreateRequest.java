package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReaderCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String studentNo;
    @NotBlank
    @Size(max = 50)
    private String name;
    @Size(max = 100)
    private String college;
    @Pattern(regexp = "^$|^1\\d{10}$", message = "必须是11位手机号码")
    @Size(max = 30)
    private String phone;
    private Long userId;

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
