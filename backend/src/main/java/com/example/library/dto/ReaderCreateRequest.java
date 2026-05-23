package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;

public class ReaderCreateRequest {
    @NotBlank
    private String studentNo;
    @NotBlank
    private String name;
    private String college;
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
