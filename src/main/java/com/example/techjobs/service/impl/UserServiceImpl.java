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
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.inputDTO.InputFileDTO;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.entity.User;
import com.example.techjobs.repository.ApplyJpaRepository;
import com.example.techjobs.repository.UserJpaRepository;
import com.example.techjobs.service.FileService;
import com.example.techjobs.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final AttributeEncryptor attributeEncryptor;
    private final ApplyJpaRepository applyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final EmailServiceImpl emailService;
    private final GenericMapper genericMapper;
    private final FileService fileService;
    private final Cloudinary cloudinary;

    @Autowired
    public UserServiceImpl(
            AttributeEncryptor attributeEncryptor,
            ApplyJpaRepository applyJpaRepository,
            UserJpaRepository userJpaRepository,
            EmailServiceImpl emailService,
            GenericMapper genericMapper,
            FileService fileService,
            Cloudinary cloudinary) {
        this.attributeEncryptor = attributeEncryptor;
        this.applyJpaRepository = applyJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.emailService = emailService;
        this.genericMapper = genericMapper;
        this.fileService = fileService;
        this.cloudinary = cloudinary;
    }

    @Override
    public OutputUserDTO findById(Integer userId) {
        User user =
                userJpaRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
        OutputUserDTO outputUserDTO = genericMapper.mapToType(user, OutputUserDTO.class);
        if (outputUserDTO != null) {
            outputUserDTO.setCv(fileService.findByUserId(userId));
            outputUserDTO.setNumberApply(applyJpaRepository.countNumberApplyByUser(userId));
        }
        return outputUserDTO;
    }

    @Override
    public Map<String, Object> loginAccount(LoginRequest data) {
        User user = userJpaRepository
                .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
                .orElse(null);

        if (Objects.isNull(user) || !attributeEncryptor.matches(data.getPassword(), user.getPassword())) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("accountId", user.getId());
        result.put("verify", !user.getState().equals(StateConstant.WAIT.name()));
        return result;
    }

    @Override
    public Boolean checkVerifyCode(Integer accountId, String verifyCode) {
        User user =
                userJpaRepository.findByIdAndStateNot(accountId, StateConstant.DELETED.name()).orElse(null);
        if (user != null && attributeEncryptor.matches(verifyCode, user.getVerifyCode())) {
            user.setState(StateConstant.ACTIVE.name());
            user.setUpdateDate(LocalDate.now());
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @SneakyThrows
    public boolean createUser(InputUserDTO data) {
        User user =
                userJpaRepository
                        .findByEmailAndStateNot(data.getEmail(), StateConstant.DELETED.name())
                        .orElse(null);
        if (user == null) {
            String verifyCode = Utils.createVerifyCode();
            user = genericMapper.mapToType(data, User.class);
            user.setAvatar(ImgConstant.UnknownUser.getValue());
            user.setPassword(attributeEncryptor.convertToDatabaseColumn(data.getPassword()));
            user.setVerifyCode(attributeEncryptor.convertToDatabaseColumn(verifyCode));
            user.setRole(RoleConstant.USER.name());
            user.setState(StateConstant.WAIT.name());
            user.setCreateBy("unknow");
            user.setCreateDate(LocalDate.now());
            user.setUpdateBy("unknow");
            user.setUpdateDate(LocalDate.now());
            emailService.sendEmailVerifyCode(data.getEmail(), verifyCode);
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @SneakyThrows
    public boolean updateUser(Integer userId, InputUserDTO data) {
        User userDuplicatedEmail =
                userJpaRepository
                        .findByIdNotAndEmailAndStateNot(userId, data.getEmail(), StateConstant.DELETED.name())
                        .orElse(null);
        User user =
                userJpaRepository.findByIdAndStateNot(userId, StateConstant.DELETED.name()).orElse(null);
        if (userDuplicatedEmail == null && user != null) {
            genericMapper.copyNonNullProperties(data, user);
            String name = Utils.formatFileName(user.getEmail()) + "_" + System.currentTimeMillis();
            if (data.getFileAvatar() != null && !data.getFileAvatar().isEmpty()) {
                Transformation incoming =
                        new Transformation<>()
                                .gravity("face")
                                .height(400)
                                .width(400)
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
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<User> findAll() {
        return this.userJpaRepository.findAllByRoleAndStateNot(
                RoleConstant.USER.name(), StateConstant.DELETED.name());
    }

    @Override
    public ResultDTO<User> delete(Integer id) {
        Optional<User> userOPT = this.userJpaRepository.findById(id);
        if (userOPT.isEmpty()) {
            return new ResultDTO<>(null, true, "Kh??ng t??m th???y user");
        }
        User user = userOPT.get();
        user.setState(StateConstant.DELETED.name());
        this.userJpaRepository.save(user);
        return new ResultDTO<>(null, false, "X??a user th??nh c??ng");
    }
}
