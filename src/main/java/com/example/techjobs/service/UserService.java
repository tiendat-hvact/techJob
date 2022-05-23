package com.example.techjobs.service;

import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import java.util.Map;

public interface UserService {

  /** Tìm tài khoản người tìm việc theo ID */
  OutputUserDTO findById(Integer userId);

  /** Kiểm tra tài khoản login */
  Map<String, Object> loginAccount(LoginRequest data);

  /** Kiểm tra verifyCode tài khoản login */
  Boolean checkVerifyCode(Integer accountId, String verifyCode);

  /** Tạo mới tài khoản người tìm việc */
  boolean createUser(InputUserDTO data);

  /** Cập nhật tài khoản người tìm việc theo ID */
  boolean updateUser(Integer userId, InputUserDTO data);
}
