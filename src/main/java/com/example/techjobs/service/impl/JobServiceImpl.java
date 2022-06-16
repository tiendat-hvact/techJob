package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.GenderConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.Company;
import com.example.techjobs.entity.Job;
import com.example.techjobs.repository.ApplyJpaRepository;
import com.example.techjobs.repository.CompanyJpaRepository;
import com.example.techjobs.repository.JobJpaRepository;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.service.JobService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobServiceImpl implements JobService {

  private final CompanyJpaRepository companyJpaRepository;
  private final ApplyJpaRepository applyJpaRepository;
  private final JobJpaRepository jopJpaRepository;
  private final JobRepository jobRepository;
  private final GenericMapper genericMapper;

  public JobServiceImpl(
      CompanyJpaRepository companyJpaRepository,
      ApplyJpaRepository applyJpaRepository,
      JobJpaRepository jopJpaRepository,
      JobRepository jobRepository, GenericMapper genericMapper) {
    this.companyJpaRepository = companyJpaRepository;
    this.applyJpaRepository = applyJpaRepository;
    this.jopJpaRepository = jopJpaRepository;
    this.jobRepository = jobRepository;
    this.genericMapper = genericMapper;
  }

  @Override
  public OutputJobDTO findById(Integer jobId) {
    Job job =
        jopJpaRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job != null) {
      job.setGender(GenderConstant.valueOf(job.getGender()).getValue());
    }
    return genericMapper.mapToType(job, OutputJobDTO.class);
  }

  @Override
  public List<OutputJobDTO> findLimit(InputJobDTO job, Integer limit) {
    if (Objects.isNull(job.getName())) {
      job.setName("");
    }
    Page<Job> jobs =
        jopJpaRepository.searchJob(
            "%" + job.getName() + "%",
            job.getTypeId(),
            StateConstant.DELETED.name(),
            PageRequest.of(0, limit));
    return genericMapper.mapToListOfType(jobs.getContent(), OutputJobDTO.class);
  }

  @Override
  public Page<OutputJobDTO> getPageableJobByCompanyId(
      SearchRequest searchRequest, Integer page, Integer size) {
    Page<Job> jobs =
        jobRepository.getPageableJobByCondition(
            searchRequest,
            StateConstant.DELETED.name(),
            PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createDate")));
    Page<OutputJobDTO> outputJobDTOS =
        genericMapper.toPage(
            jobs,
            OutputJobDTO.class,
            PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createdDate")));
    if (outputJobDTOS != null && !outputJobDTOS.isEmpty()) {
      outputJobDTOS.forEach(i -> i.setNumberApply(applyJpaRepository.countNumberApply(i.getId())));
    }
    return outputJobDTOS;
  }

  @Override
  @Transactional
  public Integer createJob(Integer companyId, InputJobDTO data) {
    Company company =
        companyJpaRepository
            .findByIdAndStateNot(companyId, StateConstant.DELETED.name())
            .orElse(null);
    if (company != null) {
      Job job = genericMapper.mapToType(data, Job.class);
      job.setCompany(company);
      job.setState(StateConstant.ACTIVE.name());
      job.setCreateBy(company.getName());
      job.setCreateDate(LocalDate.now());
      job.setUpdateBy(company.getName());
      job.setUpdateDate(LocalDate.now());
      jopJpaRepository.save(job);
      return job.getId();
    }
    return 0;
  }

  @Override
  @Transactional
  public boolean updateJob(Integer jobId, InputJobDTO data) {
    Job jobDuplicatedName =
        jopJpaRepository
            .findByIdNotAndNameAndStateNot(jobId, data.getName(), StateConstant.DELETED.name())
            .orElse(null);
    Job job =
        jopJpaRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (jobDuplicatedName == null && job != null) {
      genericMapper.copyNonNullProperties(data, job);
      job.setUpdateDate(LocalDate.now());
      jopJpaRepository.save(job);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public boolean deleteJob(Integer jobId) {
    Job job =
        jopJpaRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job != null) {
      job.setState(StateConstant.DELETED.name());
      job.setUpdateDate(LocalDate.now());
      jopJpaRepository.save(job);
      return true;
    }
    return false;
  }

  @Override
  public List<Job> findAll() {
    return this.jopJpaRepository.findAllByStateNot(StateConstant.ACTIVE.name());
  }

  @Override
  public ResultDTO<Job> delete(Integer id) {
    Optional<Job> opt = this.jopJpaRepository.findById(id);
    if (opt.isEmpty()) {
      return new ResultDTO<>(null, true, "Không tìm thấy tin tuyển dụng");
    }
    Job job = opt.get();
    job.setState(StateConstant.DELETED.name());
    this.jopJpaRepository.save(job);
    return new ResultDTO<>(null, false, "Xoá tin tuyển dụng thành công");
  }
}
