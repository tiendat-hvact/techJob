package com.example.techjobs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImgConstant {
  Prefix("https://res.cloudinary.com/dpcd1ks3v/image/upload/"),
  UnknownUser("https://res.cloudinary.com/dpcd1ks3v/image/upload/v1653189947/user/unknow_user.jpg"),
  UnknownCompany(
      "https://res.cloudinary.com/dpcd1ks3v/image/upload/v1653234811/company/unknow_company.png");
  private String value;
}
