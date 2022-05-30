package com.example.techjobs.service;

public interface EmailService {

  /** Gửi email chứa mã xác thực về tài khoản */
  void sendEmailVerifyCode(String email, String verifyCode);

  /** Gửi email thông báo công ty về người ứng tuyển công việc */
  void sendEmailNotifyJobApplied(String email, String jobName, String userCV);
}
