package com.example.techjobs.dto.inputDTO;

import lombok.Data;

@Data
public class InputJobDTO {
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
  private Integer companyId;
  private Integer typeId;
}
