package com.example.techjobs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleConstant {
  USER("USER"),
  ADMIN("ADMIN");
  private String value;
}
