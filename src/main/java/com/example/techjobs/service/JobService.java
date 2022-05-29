package com.example.techjobs.service;

import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;

public interface JobService {

  /** Tìm tin tuyển dụng theo ID */
  OutputJobDTO findById(Integer jobId);

  /** Tạo mới tin tuyển dụng */
  Integer createJob(Integer companyId, InputJobDTO data);
}
