package com.example.techjobs.service;

import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import java.util.List;

public interface JobService {

  /** Tìm tin tuyển dụng theo ID */
  OutputJobDTO findById(Integer jobId);

  /** Lấy ra danh sách tin tuyển dụng có giới hạn */
  List<OutputJobDTO> findLimit(Integer limit);

  /** Tạo mới tin tuyển dụng */
  Integer createJob(Integer companyId, InputJobDTO data);
}
