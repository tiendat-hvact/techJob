package com.example.techjobs.service.impl;

import com.example.techjobs.service.EmailService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;

  @Autowired
  public EmailServiceImpl(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @SneakyThrows
  @Override
  public void sendEmailVerifyCode(String email, String verifyCode) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
    String htmlMsg =
        "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "<head>\n"
            + "  <meta charset=\"UTF-8\">\n"
            + "  <title>Title</title>\n"
            + "</head>\n"
            + "<body>\n"
            + "<div>\n"
            + "  <p>Kính gửi chủ tài khoản <span style=\"color: red; font-weight: bold\">"
            + email
            + "</span></p>\n"
            + "  <p>Hôm nay bạn đã đăng ký tài khoản trong hệ thống TechJobs của chúng tôi !</p>\n"
            + "  <p>Mã xác thực tài khoản của bạn là <span style=\"color: red; font-weight: bold\">"
            + verifyCode
            + "</span></p>\n"
            + "  <p>Xin hãy đăng nhập vào tài khoản và nhập mã xác thực để tài khoản có thể kích hoạt ! </p>\n"
            + "</div>\n"
            + "</body>\n"
            + "</html>";
    message.setContent(htmlMsg, "text/html; charset=UTF-8");
    helper.setTo(email);
    helper.setSubject(
        "EMAIL XÁC THỰC TÀI KHOẢN " + email + " NGÀY " + formatter.format(LocalDate.now()));
    this.javaMailSender.send(message);
  }
}
