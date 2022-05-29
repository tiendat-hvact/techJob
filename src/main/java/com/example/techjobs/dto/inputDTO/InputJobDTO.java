package com.example.techjobs.dto.inputDTO;

import java.time.LocalDate;
import javax.persistence.criteria.CriteriaBuilder.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class InputJobDTO {
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

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate deadline;

  private Integer companyId;
  private Integer typeId;
}
