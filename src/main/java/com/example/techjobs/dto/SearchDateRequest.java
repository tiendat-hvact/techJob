package com.example.techjobs.dto;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SearchDateRequest {
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate fromDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate toDate;
}
