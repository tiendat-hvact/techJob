package com.example.techjobs.service.impl;

import com.example.techjobs.common.enums.GenderConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.Company;
import com.example.techjobs.entity.Job;
import com.example.techjobs.repository.ApplyRepository;
import com.example.techjobs.repository.CompanyRepository;
import com.example.techjobs.repository.JobRepository;
import com.example.techjobs.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final CompanyRepository companyRepository;
    private final ApplyRepository applyRepository;
    private final JobRepository jobRepository;
    private final GenericMapper genericMapper;

    public JobServiceImpl(
            CompanyRepository companyRepository,
            ApplyRepository applyRepository,
            JobRepository jobRepository,
            GenericMapper genericMapper) {
        this.companyRepository = companyRepository;
        this.applyRepository = applyRepository;
        this.jobRepository = jobRepository;
        this.genericMapper = genericMapper;
    }

    @Override
    public OutputJobDTO findById(Integer jobId) {
        Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
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
        Page<Job> jobs = jobRepository.searchJob("%"+job.getName()+"%", job.getTypeId(), StateConstant.DELETED.name(), PageRequest.of(0, limit));
        return genericMapper.mapToListOfType(jobs.getContent(), OutputJobDTO.class);
    }

    @Override
    public Page<OutputJobDTO> getPageableJobByCompanyId(
            Integer companyId, Integer page, Integer size) {
        Page<Job> jobs =
                jobRepository.findAllByCompanyIdAndStateNot(
                        companyId,
                        StateConstant.DELETED.name(),
                        PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createDate")));
        Page<OutputJobDTO> outputJobDTOS =
                genericMapper.toPage(
                        jobs,
                        OutputJobDTO.class,
                        PageRequest.of(page - 1, size, Sort.by(Direction.DESC, "createdDate")));
        if (outputJobDTOS != null && !outputJobDTOS.isEmpty()) {
            outputJobDTOS.forEach(i -> i.setNumberApply(applyRepository.countNumberApply(i.getId())));
        }
        return outputJobDTOS;
    }

    @Override
    @Transactional
    public Integer createJob(Integer companyId, InputJobDTO data) {
        Company company =
                companyRepository.findByIdAndStateNot(companyId, StateConstant.DELETED.name()).orElse(null);
        if (company != null) {
            Job job = genericMapper.mapToType(data, Job.class);
            job.setCompany(company);
            job.setState(StateConstant.ACTIVE.name());
            job.setCreateBy(company.getName());
            job.setCreateDate(LocalDate.now());
            job.setUpdateBy(company.getName());
            job.setUpdateDate(LocalDate.now());
            jobRepository.save(job);
            return job.getId();
        }
        return 0;
    }

    @Override
    @Transactional
    public boolean updateJob(Integer jobId, InputJobDTO data) {
        Job jobDuplicatedName =
                jobRepository
                        .findByIdNotAndNameAndStateNot(jobId, data.getName(), StateConstant.DELETED.name())
                        .orElse(null);
        Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
        if (jobDuplicatedName == null && job != null) {
            genericMapper.copyNonNullProperties(data, job);
            job.setUpdateDate(LocalDate.now());
            jobRepository.save(job);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteJob(Integer jobId) {
        Job job = jobRepository.findByIdAndStateNot(jobId, StateConstant.DELETED.name()).orElse(null);
        if (job != null) {
            job.setState(StateConstant.DELETED.name());
            job.setUpdateDate(LocalDate.now());
            jobRepository.save(job);
            return true;
        }
        return false;
    }

    @Override
    public List<Job> findAll() {
      return this.jobRepository.findActiveJob();
    }

    @Override
    public ResultDTO delete(Integer id) {
        Optional<Job> opt = this.jobRepository.findById(id);
        if (!opt.isPresent()) {
            return new ResultDTO(null, true, "Không tìm thấy tin tuyển dụng");
        }
        Job job = opt.get();
        job.setState(StateConstant.DELETED.name());
        this.jobRepository.save(job);
        return new ResultDTO(null, false, "Xoá tin tuyển dụng thành công");
    }
}
