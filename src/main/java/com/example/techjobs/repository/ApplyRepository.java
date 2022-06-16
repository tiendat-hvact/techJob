package com.example.techjobs.repository;

import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, ApplyId> {

  @Query(value = "SELECT COUNT(a) FROM Apply a WHERE a.job.id = :jobId")
  Integer countNumberApply(@Param(value = "jobId") Integer jobId);

  @Query(nativeQuery = true, value = "SELECT * FROM applies WHERE job_id = ?1")
  List<Apply> findByJobId(Integer jobId);
}
