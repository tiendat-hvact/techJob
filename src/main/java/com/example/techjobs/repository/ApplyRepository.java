package com.example.techjobs.repository;

import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.entity.Apply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplyRepository {
  Page<Apply> getPageableApplyByCondition(SearchRequest searchRequest, Pageable pageable);
}
