package com.example.techjobs.service;

import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.outputDTO.OutputApplyDTO;
import com.example.techjobs.entity.ApplyId;
import org.springframework.data.domain.Page;

public interface ApplyService {

  /** Nộp đơn ứng tuyển công việc */
  Integer applyJob(Integer jobId, Integer userId);

  /** Lấy ra danh sách nộp đơn ứng tuyển theo điều kiện có phân trang */
  Page<OutputApplyDTO> getPageableApplyByCondition(
      SearchRequest searchRequest, Integer page, Integer size);

  /** Xóa thông tin ứng tuyển công việc */
  boolean deleteApply(ApplyId applyId);
}
