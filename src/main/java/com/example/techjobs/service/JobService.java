package com.example.techjobs.service;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.Job;
import java.util.List;
import org.springframework.data.domain.Page;

public interface JobService {

  /** Tìm tin tuyển dụng theo ID */
  OutputJobDTO findById(Integer jobId);

  /** Lấy ra danh sách tin tuyển dụng có giới hạn */
  List<OutputJobDTO> findLimit(InputJobDTO job, Integer limit);

  /** Lấy ra danh sách tin tuyển dụng có theo Nhà tuyển dụng có phân trang */
  Page<OutputJobDTO> getPageableJobByCompanyId(SearchRequest searchRequest, Integer page, Integer size);

  /** Tạo mới tin tuyển dụng */
  Integer createJob(Integer companyId, InputJobDTO data);

  /** Cập nhật thông tin tuyển dụng theo ID */
  boolean updateJob(Integer jobId, InputJobDTO data);

  /** Xóa tin tuyển dụng theo ID */
  boolean deleteJob(Integer jobId);

  List<Job> findAll();

  ResultDTO<Job> delete(Integer id);
}
