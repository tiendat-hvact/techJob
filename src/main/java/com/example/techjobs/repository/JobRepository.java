package com.example.techjobs.repository;

import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository {

  Page<Job> getPageableJobByCondition(
      SearchRequest searchRequest, String stateNot, Pageable pageable);
}
