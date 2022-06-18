package com.example.techjobs.repository;

import com.example.techjobs.entity.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyJpaRepository extends JpaRepository<Company, Integer> {

  Optional<Company> findByIdAndStateNot(Integer companyId, String stateNot);

  Optional<Company> findByEmailAndStateNot(String email, String stateNot);

  Optional<Company> findByIdNotAndEmailAndStateNot(
      Integer companyId, String email, String stateNot);

  Page<Company> findAllByStateNot(String stateNot, Pageable pageable);

  List<Company> findAllByStateNot(String stateNot);

  @Query(nativeQuery = true, value = "SELECT * FROM companies JOIN followings ON companies.id = followings.company_id WHERE followings.user_id = ?1 AND companies.state <> 'DELETED'")
  List<Company> getCompanyFollowing(Integer userId);

}
