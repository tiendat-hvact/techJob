package com.example.techjobs.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.techjobs.common.encryptor.AttributeEncryptor;
import com.example.techjobs.common.enums.ImgConstant;
import com.example.techjobs.common.enums.RoleConstant;
import com.example.techjobs.common.enums.StateConstant;
import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.inputDTO.InputFileDTO;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.UserRepository;
import com.example.techjobs.service.FileService;
import com.example.techjobs.service.UserService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final Cloudinary cloudinary;
  private final FileService fileService;
  private final GenericMapper genericMapper;
  private final UserRepository userRepository;
  private final EmailServiceImpl emailService;
  private final AttributeEncryptor attributeEncryptor;

  @Autowired
  public UserServiceImpl(
      Cloudinary cloudinary,
      FileService fileService,
      GenericMapper genericMapper,
      UserRepository userRepository,
      EmailServiceImpl emailService,
      AttributeEncryptor attributeEncryptor) {
    this.cloudinary = cloudinary;
    this.fileService = fileService;
    this.genericMapper = genericMapper;
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.attributeEncryptor = attributeEncryptor;
  }

  @Override
  public OutputUserDTO findById(Integer userId) {
    User user =
        userRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    return genericMapper.mapToType(user, OutputUserDTO.class);
  }

  @Override
  public Map<String, Object> loginAccount(LoginRequest data) {
    Map<String, Object> result = null;
    User user =
        userRepository
            .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    if (user != null) {
      if (attributeEncryptor.matches(data.getPassword(), user.getPassword())) {
        result = new HashMap<>();
        result.put("accountId", user.getId());
        if (user.getState().equals(StateConstant.WAIT.name())) {
          result.put("verify", false);
        } else {
          result.put("verify", true);
        }
      }
    }
    return result;
  }

  @Override
  public Boolean checkVerifyCode(Integer accountId, String verifyCode) {
    User user =
        userRepository
            .findByIdAndStateNot(accountId, StateConstant.DELETED.name())
            .orElse(null);
    if (user != null && attributeEncryptor.matches(verifyCode, user.getVerifyCode())) {
      user.setState(StateConstant.ACTIVE.name());
      user.setUpdateDate(LocalDate.now());
      userRepository.save(user);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  @SneakyThrows
  public boolean createUser(InputUserDTO data) {
    User user =
        userRepository
            .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    if (user == null) {
      String verifyCode = Utils.createVerifyCode();
      user = genericMapper.mapToType(data, User.class);
      user.setAvatar(ImgConstant.UnknownUser.getValue());
      user.setPassword(attributeEncryptor.convertToDatabaseColumn(data.getPassword()));
      user.setVerifyCode(attributeEncryptor.convertToDatabaseColumn(verifyCode));
      user.setRole(RoleConstant.USER.getValue());
      user.setState(StateConstant.WAIT.name());
      user.setCreateBy("unknow");
      user.setCreateDate(LocalDate.now());
      user.setUpdateBy("unknow");
      user.setUpdateDate(LocalDate.now());
      emailService.sendEmailVerifyCode(data.getEmail(), verifyCode);
      userRepository.save(user);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  @SneakyThrows
  public boolean updateUser(Integer userId, InputUserDTO data) {
    User userDuplicatedEmail =
        userRepository
            .findByIdNotAndEmailAndStateNot(userId, data.getEmail(), StateConstant.DELETED.name())
            .orElse(null);
    User user =
        userRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
    if (userDuplicatedEmail == null && user != null) {
      genericMapper.copyNonNullProperties(data, user);
      String name = Utils.formatFileName(user.getEmail()) + "_" + System.currentTimeMillis();
      if (data.getFileAvatar() != null && !data.getFileAvatar().isEmpty()) {
        Transformation incoming =
            new Transformation<>()
                .gravity("face")
                .height(500)
                .width(500)
                .crop("crop")
                .chain()
                .radius("max")
                .chain()
                .width(100)
                .crop("scale");
        this.cloudinary
            .uploader()
            .upload(
                data.getFileAvatar().getBytes(),
                ObjectUtils.asMap(
                    "resource_type",
                    "auto",
                    "public_id",
                    "user/" + name,
                    "transformation",
                    incoming));
        user.setAvatar(ImgConstant.Prefix.getValue() + "user/" + name);
      }
      if (data.getFileCV() != null && !data.getFileCV().isEmpty()) {
        this.cloudinary
            .uploader()
            .upload(
                data.getFileCV().getBytes(),
                ObjectUtils.asMap("resource_type", "auto", "public_id", "cv/" + name));
        InputFileDTO dataFile = new InputFileDTO();
        dataFile.setName(name);
        dataFile.setUrl(ImgConstant.Prefix.getValue() + "cv/" + name);
        dataFile.setUserId(userId);
        fileService.createOrUpdateFile(userId, dataFile);
      }
      user.setUpdateDate(LocalDate.now());
      userRepository.save(user);
      return true;
    }
    return false;
  }
}
