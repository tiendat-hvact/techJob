package com.example.techjobs.service;

public interface EmailService {

  /** Gửi email chứa mã xác thực về tài khoản người dùng */
  void sendEmailVerifyCode(String email, String verifyCode);
}
