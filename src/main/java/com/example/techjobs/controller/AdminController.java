package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/techJob/admin")
public class AdminController {

  private final GenericMapper genericMapper;
  private final UserService userService;

  @Autowired
  public AdminController(GenericMapper genericMapper, UserService userService) {
    this.genericMapper = genericMapper;
    this.userService = userService;
  }

  @GetMapping
  public String userFindById(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "admin") int adminId) {
    if (adminId != 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputUserDTO admin = userService.findById(adminId);
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
    model.addAttribute("admin", genericMapper.mapToType(admin, InputUserDTO.class));
    model.addAttribute("avatar", admin.getAvatar());
    model.addAttribute("notification", notification);
    return "admin-info";
  }

  @PostMapping("/update-info")
  public String userUpdate(
      @CookieValue(name = "admin") int adminId, @ModelAttribute InputUserDTO data) {
    if (userService.updateUser(adminId, data)) {
      return "redirect:/techJob/admin";
    } else {
      return "redirect:/techJob/admin?text=update-fail";
    }
  }
}
