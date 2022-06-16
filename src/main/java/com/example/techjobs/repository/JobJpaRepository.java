package com.example.techjobs.repository;

import com.example.techjobs.entity.Job;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobJpaRepository extends JpaRepository<Job, Integer> {

  Optional<Job> findByIdAndStateNot(Integer jobId, String stateNot);

  Optional<Job> findByIdNotAndNameAndStateNot(Integer jobId, String name, String stateNot);
  
  Page<Job> findAllByCompanyIdAndStateNot(Integer companyId, String stateNot, Pageable pageable);

  @Query(
      value =
          "SELECT j FROM Job j"
              + " WHERE (:name IS NULL OR j.name LIKE :name)"
              + " AND (:typeId IS NULL OR j.type.id = :typeId)"
              + " AND j.state <> :state"
              + " ORDER BY j.createDate DESC")
  Page<Job> searchJob(
      @Param("name") String ex,
      @Param("typeId") Integer typeId,
      @Param("state") String stateNot,
      Pageable pageable);

  List<Job> findAllByTypeId(Integer typeId);

  List<Job> findAllByStateNot(String stateNot);
}