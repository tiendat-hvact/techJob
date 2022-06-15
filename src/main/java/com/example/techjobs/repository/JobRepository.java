package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {

  Optional<Job> findByIdAndStateNot(Integer jobId, String stateNot);

  Optional<Job> findByIdNotAndNameAndStateNot(Integer jobId, String name, String stateNot);

  Page<Job> findAllByStateNot(String stateNot, Pageable pageable);

  Page<Job> findAllByCompanyIdAndStateNot(Integer companyId, String stateNot, Pageable pageable);
}
