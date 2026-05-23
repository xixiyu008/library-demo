package com.example.library.vo;

import com.example.library.enums.ReaderStatus;

public class ReaderVO {
    private Long id;
    private String studentNo;
    private String name;
    private String college;
    private String phone;
    private ReaderStatus status;
    private Long userId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public ReaderStatus getStatus() { return status; }
    public void setStatus(ReaderStatus status) { this.status = status; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
