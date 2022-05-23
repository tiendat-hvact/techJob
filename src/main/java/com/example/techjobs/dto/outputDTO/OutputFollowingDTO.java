package com.example.techjobs.dto.outputDTO;

import lombok.Data;

@Data
public class OutputFollowingDTO {
  private Integer id;
  private OutputUserDTO user;
  private OutputCompanyDTO company;
  private OutputJobDTO job;
}
