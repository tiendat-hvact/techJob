package com.example.techjobs.dto;

import lombok.Data;

@Data
public class SearchRequest {
  private Integer companyId;
  private Integer userId;
  private Integer jobId;
  private String state;
  private String name;

  private SearchDateRequest createDate;
  private SearchDateRequest deadLine;
}
