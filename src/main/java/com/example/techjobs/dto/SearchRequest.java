package com.example.techjobs.dto;

import lombok.Data;

@Data
public class SearchRequest {
  private Integer companyId;
  private String name;
  private SearchDateRequest createDate;
  private SearchDateRequest deadLine;
  private String state;
}
