package com.example.techjobs.repository.impl;

import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.entity.Job;
import com.example.techjobs.repository.JobRepository;
import java.time.LocalDate;
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
public class JobRepositoryImpl implements JobRepository {

  @PersistenceContext EntityManager entityManager;

  @Override
  public Page<Job> getPageableJobByCondition(SearchRequest searchRequest, Pageable pageable) {
    try {
      String sql = "SELECT j FROM Job j WHERE j.state <> '" + StateConstant.DELETED.name() + "'";
      if (searchRequest.getCompanyId() != null) {
        sql += " AND j.company.id = " + searchRequest.getCompanyId();
      }
      if (searchRequest.getName() != null && !Utils.isNullOrEmpty(searchRequest.getName())) {
        sql += "AND j.name LIKE '%" + searchRequest.getName() + "%'";
      }
      if (searchRequest.getCreateDate() != null
          && searchRequest.getCreateDate().getFromDate() != null
          && searchRequest.getCreateDate().getToDate() != null) {
        sql +=
            " AND j.createDate BETWEEN '"
                + searchRequest.getCreateDate().getFromDate()
                + "' AND '"
                + searchRequest.getCreateDate().getToDate()
                + "'";
      }
      if (searchRequest.getDeadLine() != null
          && searchRequest.getDeadLine().getFromDate() != null
          && searchRequest.getDeadLine().getToDate() != null) {
        sql +=
            " AND j.createDate BETWEEN '"
                + searchRequest.getDeadLine().getFromDate()
                + "' AND '"
                + searchRequest.getDeadLine().getToDate()
                + "'";
      }
      if (searchRequest.getState() != null && !Utils.isNullOrEmpty(searchRequest.getState())) {
        if (searchRequest.getState().equals("beforeDeadline")) {
          sql += "AND j.deadline > '" + LocalDate.now() + "'";
        } else {
          sql += "AND j.deadline <= '" + LocalDate.now() + "'";
        }
      }
      log.info(sql);
      Query query = entityManager.createQuery(sql, Job.class);
      List<Job> jobs = query.getResultList();
      final int start = (int) pageable.getOffset();
      final int end = Math.min((start + pageable.getPageSize()), jobs.size());
      return new PageImpl<>(jobs.subList(start, end), pageable, jobs.size());
    } catch (Exception e) {
      return null;
    }
  }
}
