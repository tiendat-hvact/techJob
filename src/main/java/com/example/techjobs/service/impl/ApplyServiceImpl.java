package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.outputDTO.OutputApplyDTO;
import com.example.techjobs.dto.outputDTO.OutputFileDTO;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.entity.File;
import com.example.techjobs.entity.Job;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.ApplyJpaRepository;
import com.example.techjobs.repository.ApplyRepository;
import com.example.techjobs.repository.FileJpaRepository;
import com.example.techjobs.repository.JobJpaRepository;
import com.example.techjobs.repository.UserJpaRepository;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.EmailService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class ApplyServiceImpl implements ApplyService {

  private final ApplyJpaRepository applyJpaRepository;
  private final FileJpaRepository fileJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final JobJpaRepository jobJpaRepository;
  private final ApplyRepository applyRepository;
  private final GenericMapper genericMapper;
  private final EmailService emailService;

  @Autowired
  public ApplyServiceImpl(
      ApplyJpaRepository applyJpaRepository,
      FileJpaRepository fileJpaRepository,
      UserJpaRepository userJpaRepository,
      JobJpaRepository jobJpaRepository,
      ApplyRepository applyRepository,
      GenericMapper genericMapper,
      EmailService emailService) {
    this.applyJpaRepository = applyJpaRepository;
    this.fileJpaRepository = fileJpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.jobJpaRepository = jobJpaRepository;
    this.applyRepository = applyRepository;
    this.genericMapper = genericMapper;
    this.emailService = emailService;
  }

  @Override
  public Integer applyJob(Integer jobId, Integer userId) {
    Job job =
        jobJpaRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
    if (job == null) return 1;
    if (job.getDeadline().isBefore(LocalDate.now())) return 2;
    User user =
        userJpaRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    if (user == null) return 3;
    File file = fileJpaRepository.findByUserId(userId).orElse(null);
    if (file == null) return 4;
    ApplyId applyId = new ApplyId(userId, jobId);
    Apply apply = applyJpaRepository.findById(applyId).orElse(null);
    if (apply != null) return 5;
    apply = new Apply(applyId, user, job, LocalDate.now());
    applyJpaRepository.save(apply);
    emailService.sendEmailNotifyJobApplied(
        job.getCompany().getEmail(), job.getName(), file.getUrl());
    return 0;
  }

  @Override
  public Page<OutputApplyDTO> getPageableApplyByCondition(
      SearchRequest searchRequest, Integer page, Integer size) {
    Page<Apply> applies =
        applyRepository.getPageableApplyByCondition(
            searchRequest, PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createDate")));
    Page<OutputApplyDTO> outputApplyDTOS =
        genericMapper.toPage(
            applies,
            OutputApplyDTO.class,
            PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createdDate")));
    if (outputApplyDTOS != null && !outputApplyDTOS.isEmpty()) {
      outputApplyDTOS.forEach(
          i ->
              fileJpaRepository
                  .findByUserId(i.getUser().getId())
                  .ifPresent(
                      file ->
                          i.getUser().setCv(genericMapper.mapToType(file, OutputFileDTO.class))));
    }
    return outputApplyDTOS;
  }

  @Override
  public boolean deleteApply(ApplyId applyId) {
    Apply apply = applyJpaRepository.findById(applyId).orElse(null);
    if (apply != null) {
      applyJpaRepository.delete(apply);
      return true;
    }
    return false;
  }
}
