package com.example.techjobs.dto.outputDTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class OutputCompanyDTO {
  private Integer id;
  private String avatar;
  private String name;
  private String email;
  private String password;
  private String phone;
  private String city;
  private String address;
  private String introduce;
  private String verifyCode;
  private String state;
  private String createBy;
  private LocalDate createDate;
  private String updateBy;
  private LocalDate updateDate;
}
