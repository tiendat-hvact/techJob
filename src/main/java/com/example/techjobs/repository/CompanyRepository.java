package com.example.techjobs.repository;

import com.example.techjobs.entity.Company;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

  Optional<Company> findByIdAndStateNot(Integer companyId, String stateNot);

  Optional<Company> findByEmailAndStateNot(String email, String stateNot);

  Optional<Company> findByIdNotAndEmailAndStateNot(
      Integer companyId, String email, String stateNot);

  @Query(value = "SELECT c FROM Company c WHERE c.state <> :stateNot")
  Page<Company> findAllAndStateNot(@Param(value = "stateNot") String stateNot, Pageable pageable);
}
