package com.example.techjobs.dto.outputDTO;

import com.example.techjobs.entity.Type;
import java.time.LocalDate;
import lombok.Data;

@Data
public class OutputJobDTO {
  private String name;
  private Integer numberRecruit;
  private String gender;
  private String salary;
  private String academicLevel;
  private String experience;
  private String workingForm;
  private String welfare;
  private String description;
  private String requirement;
  private LocalDate deadline;
  private String state;
  private String createBy;
  private LocalDate createDate;
  private String updateBy;
  private LocalDate updateDate;
  private OutputCompanyDTO company;
  private Type type;
}
