package com.example.techjobs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderConstant {
  Male("Nam"),
  Female("Nữ");
  private String value;
}
