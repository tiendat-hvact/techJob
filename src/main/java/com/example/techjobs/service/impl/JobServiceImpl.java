package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.GenderConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.*;
import com.example.techjobs.repository.*;
import com.example.techjobs.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final CompanyJpaRepository companyJpaRepository;
  private final ApplyJpaRepository applyJpaRepository;
  private final JobJpaRepository jopJpaRepository;
  private final JobRepository jobRepository;
  private final GenericMapper genericMapper;
  private final FollowingJpaRepository followingJpaRepository;
  private final UserJpaRepository userJpaRepository;

  @Override
  public Integer countNumberJob(Integer companyId) {
    return jopJpaRepository.countNumberJob(companyId, StateConstant.DELETED.name());
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
  public List<Job> findAll() {
    return this.jopJpaRepository.findAllByStateNot(StateConstant.DELETED.name());
  }

  @Override
  public Page<OutputJobDTO> getPageableJobByCondition(
      SearchRequest searchRequest, Integer page, Integer size) {
    Page<Job> jobs =
        jobRepository.getPageableJobByCondition(
            searchRequest, PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createDate")));
    Page<OutputJobDTO> outputJobDTOS =
        genericMapper.toPage(
            jobs,
            OutputJobDTO.class,
            PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createdDate")));
    if (outputJobDTOS != null && !outputJobDTOS.isEmpty()) {
      outputJobDTOS.forEach(
          i -> i.setNumberApply(applyJpaRepository.countNumberApplyByJob(i.getId())));
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
  public ResultDTO<Job> delete(Integer jobId) {
    Optional<Job> opt = this.jopJpaRepository.findById(jobId);
    if (opt.isEmpty()) {
      return new ResultDTO<>(null, true, "Không tìm thấy tin tuyển dụng");
    }
    Job job = opt.get();
    List<Apply> applies = applyJpaRepository.findAllByJobId(jobId);
    applyJpaRepository.deleteAll(applies);
    job.setState(StateConstant.DELETED.name());
    this.jopJpaRepository.save(job);
    return new ResultDTO<>(null, false, "Xoá tin tuyển dụng thành công");
  }

  @Override
  public void followJob(Integer jobId, Integer userId) {
    Job job = jopJpaRepository.findById(jobId).orElse(null);
    if (Objects.isNull(job)) {
      return;
    }

    User user = this.userJpaRepository.findById(userId).orElse(null);
    if (Objects.isNull(user)) {
      return;
    }

    Following following = this.followingJpaRepository.findByJobIdAndUserid(jobId, userId).orElse(null);
    if (Objects.nonNull(following)) {
      return;
    }

    following = new Following();
    following.setJob(job);
    following.setUser(user);
    following.setCreateDate(LocalDate.now());
    this.followingJpaRepository.save(following);
  }

  @Override
  public void unfollowJob(Integer jobId, Integer userId) {

    Job job = jopJpaRepository.findById(jobId).orElse(null);
    if (Objects.isNull(job)) {
      return;
    }

    User user = this.userJpaRepository.findById(userId).orElse(null);
    if (Objects.isNull(user)) {
      return;
    }

    Following following = this.followingJpaRepository.findByJobIdAndUserid(jobId, userId).orElse(null);
    if (Objects.isNull(following)) {
      return;
    }

    followingJpaRepository.deleteById(following.getId());
  }

  @Override
  public boolean checkFollowing(Integer idJob, Integer idUser) {
    Optional<Following> byJobIdAndUserid = this.followingJpaRepository.findByJobIdAndUserid(idJob, idUser);
    return byJobIdAndUserid.isPresent();
  }

  @Override
  public List<Job> getJobFollowing(Integer userId) {
    User user = this.userJpaRepository.findById(userId).orElse(null);
    if (Objects.isNull(user)) return new ArrayList<>();
    return this.jopJpaRepository.getJobFollowing(userId);
  }
}
