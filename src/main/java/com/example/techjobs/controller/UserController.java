package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputFileDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.FileService;
import com.example.techjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/techJob/user")
public class UserController {

  private final GenericMapper genericMapper;
  private final ApplyService applyService;
  private final UserService userService;
  private final FileService fileService;

  @Autowired
  public UserController(
      GenericMapper genericMapper,
      ApplyService applyService,
      UserService userService,
      FileService fileService) {
    this.genericMapper = genericMapper;
    this.applyService = applyService;
    this.userService = userService;
    this.fileService = fileService;
  }

  @GetMapping
  public String userFindById(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "user", defaultValue = "0") int userId) {
    if (userId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputUserDTO user = userService.findById(userId);
    if (user != null) {
      if (notification.getText() != null) {
        switch (notification.getText()) {
          case "update-fail":
            notification.setText(
                "Cập nhật thông tin thất bại <br> Email mới của bạn đã có trong hệ thống");
            break;
          default:
            notification.setText(null);
            break;
        }
      }
      OutputFileDTO cv = fileService.findByUserId(userId);
      model.addAttribute("user", genericMapper.mapToType(user, InputUserDTO.class));
      model.addAttribute("avatar", user.getAvatar());
      model.addAttribute("notification", notification);
      model.addAttribute("cv", cv);
      return "user-info";
    }
    return "redirect:/techJob/login?text=account-not-found";
  }

  @PostMapping("/update-info")
  public String userUpdate(
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @ModelAttribute InputUserDTO data) {
    if (userService.updateUser(userId, data)) {
      return "redirect:/techJob/user";
    } else {
      return "redirect:/techJob/user?text=update-fail";
    }
  }

  @GetMapping("/apply-job/{id}")
  public String applyJob(
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @PathVariable(name = "id") Integer jobId) {
    Integer result = applyService.applyJob(jobId, userId);
    if (result == 0) {
      return "redirect:/techJob/job/" + jobId + "?text=apply-success";
    } else if (result == 1) {
      return "redirect:/techJob?text=job-not-found";
    } else if (result == 2) {
      return "redirect:/techJob/login?text=unauthorized";
    } else if (result == 3) {
      return "redirect:/techJob/job/" + jobId + "?text=cv-none";
    } else if (result == 4) {
      return "redirect:/techJob/job/" + jobId + "?text=apply-existed";
    } else {
      return "redirect:/techJob/job/" + jobId + "?text=apply-fail";
    }
  }
}
