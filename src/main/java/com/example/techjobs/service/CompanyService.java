package com.example.techjobs.service;

import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.entity.Company;
import java.util.List;
import java.util.Map;

public interface CompanyService {

  /** Tìm tài khoản nhà tuyển dụng theo ID */
  OutputCompanyDTO findById(Integer companyId);

  /** Lấy ra danh sách nhà tuyển dụng có giới hạn */
  List<OutputCompanyDTO> findLimit(Integer limit);

  /** Lấy ra danh sách tất cả nhà tuyển dụng */
  List<Company> findAll();

  /** Kiểm tra tài khoản login */
  Map<String, Object> loginAccount(LoginRequest data);

  /** Kiểm tra verifyCode tài khoản login */
  Boolean checkVerifyCode(Integer accountId, String verifyCode);

  /** Tạo mới tài khoản nhà tuyển dụng */
  boolean createCompany(InputCompanyDTO data);

  /** Cập nhật tài khoản nhà tuyển dụng theo ID */
  boolean updateCompany(int companyId, InputCompanyDTO data);

  /** Xóa tài khoản nhà tuyển dụng theo ID */
  ResultDTO<Company> delete(Integer id);
}
