package com.example.techjobs.repository.impl;

import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.repository.ApplyRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplyRepositoryImpl implements ApplyRepository {

  @PersistenceContext EntityManager entityManager;

  @Override
  public Page<Apply> getPageableApplyByCondition(SearchRequest searchRequest, Pageable pageable) {
    try {
      String sql = "SELECT a FROM Apply a WHERE 1 = 1 ";
      if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0) {
        sql += " AND a.job.company.id = " + searchRequest.getCompanyId();
      }
      if (searchRequest.getUserId() != null && searchRequest.getUserId() != 0) {
        sql += " AND a.user.id = " + searchRequest.getUserId();
      }
      if (searchRequest.getJobId() != null && searchRequest.getJobId() != 0) {
        sql += " AND a.job.id = " + searchRequest.getJobId();
      }
      if (searchRequest.getName() != null && !Utils.isNullOrEmpty(searchRequest.getName())) {
        sql += "AND a.job.name LIKE '%" + searchRequest.getName() + "%'";
      }
      if (searchRequest.getCreateDate() != null
          && searchRequest.getCreateDate().getFromDate() != null
          && searchRequest.getCreateDate().getToDate() != null) {
        sql +=
            " AND a.createDate BETWEEN '"
                + searchRequest.getCreateDate().getFromDate()
                + "' AND '"
                + searchRequest.getCreateDate().getToDate()
                + "'";
      }
      log.info(sql);
      Query query = entityManager.createQuery(sql, Apply.class);
      List<Apply> applies = query.getResultList();
      final int start = (int) pageable.getOffset();
      final int end = Math.min((start + pageable.getPageSize()), applies.size());
      return new PageImpl<>(applies.subList(start, end), pageable, applies.size());
    } catch (Exception e) {
      return null;
    }
  }
}
