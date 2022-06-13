package com.example.techjobs.dto.inputDTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InputCompanyDTO {
  private String avatar;
  private String name;
  private String email;
  private String password;
  private String rePassword;
  private String phone;
  private String city;
  private String address;
  private String introduce;
  private MultipartFile file;
}
