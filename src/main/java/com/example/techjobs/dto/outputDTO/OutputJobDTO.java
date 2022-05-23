package com.example.techjobs.dto.outputDTO;

import lombok.Data;

@Data
public class OutputJobDTO {
  private String name;
  private Integer numberRecruit;
  private String gender;
  private String salary;
  private String academicLevel;
  private Integer experience;
  private String workingForm;
  private String welfare;
  private String description;
  private String requirement;
  private OutputCompanyDTO company;
  private Integer typeId;
}
