package com.example.techjobs.service;

import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.Job;
import java.util.List;
import org.springframework.data.domain.Page;

public interface JobService {

  /** Đếm số lượng tin tuyển dụng theo ID nhà tuyển dụng */
  Integer countNumberJob(Integer companyId);

  /** Tìm tin tuyển dụng theo ID */
  OutputJobDTO findById(Integer jobId);

  /** Lấy ra danh sách tin tuyển dụng có giới hạn */
  List<OutputJobDTO> findLimit(InputJobDTO job, Integer limit);

  /** Lấy ra danh sách tất cả tin tuyển dụng */
  List<Job> findAll();

  /** Lấy ra danh sách tin tuyển dụng theo điều kiện có phân trang */
  Page<OutputJobDTO> getPageableJobByCondition(
      SearchRequest searchRequest, Integer page, Integer size);

  /** Tạo mới tin tuyển dụng */
  Integer createJob(Integer companyId, InputJobDTO data);

  /** Cập nhật thông tin tuyển dụng theo ID */
  boolean updateJob(Integer jobId, InputJobDTO data);

  /** Xóa tin tuyển dụng */
  ResultDTO<Job> delete(Integer jobId);
}
