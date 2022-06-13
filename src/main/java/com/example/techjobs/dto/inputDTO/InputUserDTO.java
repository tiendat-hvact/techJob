package com.example.techjobs.dto.inputDTO;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InputUserDTO {
  private String avatar;
  private String name;
  private String sex;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dob;

  private String email;
  private String password;
  private String rePassword;
  private String phone;
  private String address;
  private MultipartFile fileAvatar;
  private MultipartFile fileCV;
}
