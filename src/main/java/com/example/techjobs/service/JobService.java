package com.example.techjobs.service;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import java.util.List;

import com.example.techjobs.entity.Job;
import org.springframework.data.domain.Page;

public interface JobService {

  /** Tìm tin tuyển dụng theo ID */
  OutputJobDTO findById(Integer jobId);

  /** Lấy ra danh sách tin tuyển dụng có giới hạn */
  List<OutputJobDTO> findLimit(InputJobDTO job, Integer limit);

  /** Lấy ra danh sách tin tuyển dụng có theo Nhà tuyển dụng có phân trang */
  Page<OutputJobDTO> getPageableJobByCompanyId(Integer companyId, Integer page, Integer size);

  /** Tạo mới tin tuyển dụng */
  Integer createJob(Integer companyId, InputJobDTO data);

  /** Cập nhật thông tin tuyển dụng theo ID */
  boolean updateJob(Integer jobId, InputJobDTO data);

  /** Xóa tin tuyển dụng theo ID */
  boolean deleteJob(Integer jobId);

  List<Job> findAll();

  ResultDTO delete(Integer id);
}
