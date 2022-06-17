package com.example.techjobs.dto.outputDTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class OutputApplyDTO {
  private OutputUserDTO user;
  private OutputJobDTO job;
  private LocalDate createDate;
}
