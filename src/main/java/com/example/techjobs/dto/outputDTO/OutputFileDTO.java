package com.example.techjobs.dto.outputDTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class OutputFileDTO {
  private Integer id;
  private String name;
  private String url;
  private OutputUserDTO user;
  private LocalDate createDate;
}
