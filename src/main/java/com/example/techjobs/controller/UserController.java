package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputFileDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.service.FileService;
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
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private final FileService fileService;
  private final GenericMapper genericMapper;

  @Autowired
  public UserController(
      UserService userService, FileService fileService, GenericMapper genericMapper) {
    this.userService = userService;
    this.fileService = fileService;
    this.genericMapper = genericMapper;
  }

  @GetMapping
  public String userFindById(
      Model model, @CookieValue(name = "userId", defaultValue = "0") int userId) {
    if (userId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputUserDTO user = userService.findById(userId);
    if (user != null) {
      OutputFileDTO cv = fileService.findByUserId(userId);
      model.addAttribute("user", genericMapper.mapToType(user, InputUserDTO.class));
      model.addAttribute("avatar", user.getAvatar());
      model.addAttribute("cv", cv);
      return "user-info";
    }
    return "redirect:/techJob/login?text=account-not-found";
  }

  @PostMapping("/update-info")
  public String userUpdate(
      @CookieValue(name = "userId", defaultValue = "0") int userId,
      @ModelAttribute InputUserDTO data) {
    userService.updateUser(userId, data);
    return "redirect:/user";
  }
}
