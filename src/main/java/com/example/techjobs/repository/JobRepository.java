package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Integer> {

  Optional<Job> findByIdAndStateNot(Integer jobId, String name);

  @Query(value = "SELECT j FROM Job j WHERE j.state <> :stateNot")
  Page<Job> findAllAndStateNot(@Param(value = "stateNot") String stateNot, Pageable pageable);
}
