package com.example.techjobs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateConstant {
  WAIT("Chờ xác thực"),
  ACTIVE("Hoạt động"),
  DELETED("Xóa");
  private String value;
}
