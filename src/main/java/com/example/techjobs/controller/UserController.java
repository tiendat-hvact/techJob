package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputApplyDTO;
import com.example.techjobs.dto.outputDTO.OutputUserDTO;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  @Autowired
  public UserController(
      GenericMapper genericMapper, ApplyService applyService, UserService userService) {
    this.genericMapper = genericMapper;
    this.applyService = applyService;
    this.userService = userService;
  }

  @GetMapping
  public String userFindById(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "user", defaultValue = "0") Integer userId) {
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
      model.addAttribute("user", genericMapper.mapToType(user, InputUserDTO.class));
      model.addAttribute("numberApply", user.getNumberApply());
      model.addAttribute("notification", notification);
      model.addAttribute("cv", user.getCv());
      return "user-info";
    }
    return "redirect:/techJob/login?text=account-not-found";
  }

  @PostMapping("/update-info")
  public String userUpdate(
      @CookieValue(name = "user", defaultValue = "0") Integer userId,
      @ModelAttribute InputUserDTO data) {
    if (userService.updateUser(userId, data)) {
      return "redirect:/techJob/user";
    } else {
      return "redirect:/techJob/user?text=update-fail";
    }
  }

  @GetMapping("/apply-job/{id}")
  public String applyJob(
      @CookieValue(name = "user", defaultValue = "0") Integer userId,
      @PathVariable(name = "id") Integer jobId) {
    Integer result = applyService.applyJob(jobId, userId);
    if (result == 0) {
      return "redirect:/techJob/job/" + jobId + "?text=apply-success";
    } else if (result == 1) {
      return "redirect:/techJob?text=job-not-found";
    } else if (result == 2) {
      return "redirect:/techJob/job/" + jobId + "?text=over-deadline";
    } else if (result == 3) {
      return "redirect:/techJob/login?text=unauthorized";
    } else if (result == 4) {
      return "redirect:/techJob/job/" + jobId + "?text=cv-none";
    } else if (result == 5) {
      return "redirect:/techJob/job/" + jobId + "?text=apply-existed";
    } else {
      return "redirect:/techJob/job/" + jobId + "?text=apply-fail";
    }
  }

  @RequestMapping("/apply-management/page/{pageId}")
  public String candidateManagement(
      Model model,
      @PathVariable(name = "pageId") Integer pageId,
      @CookieValue(name = "user", defaultValue = "0") Integer userId,
      @ModelAttribute(name = "searchRequest") SearchRequest searchRequest,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (userId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    int size = 5;
    searchRequest.setUserId(userId);
    Page<OutputApplyDTO> outputApplyDTOS =
        applyService.getPageableApplyByCondition(searchRequest, pageId, size);
    if (outputApplyDTOS == null || outputApplyDTOS.isEmpty()) {
      model.addAttribute("page", 0);
      model.addAttribute("size", 0);
      model.addAttribute("pageId", 0);
      model.addAttribute("stt", 0);
      model.addAttribute("candidates", null);
    } else {
      int page = outputApplyDTOS.getTotalPages();
      model.addAttribute("page", page);
      model.addAttribute("size", size);
      model.addAttribute("pageId", pageId);
      model.addAttribute("stt", Utils.getListNumberPage(page));
      model.addAttribute("candidates", outputApplyDTOS);
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "delete-success":
          notification.setText("Xóa thông tin ứng tuyển thành công");
          break;
        case "delete-fail":
          notification.setText(
              "Xóa thông tin ứng tuyển thất bại <br> Xin hãy chắc chắn thông tin có tồn tại");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("notification", notification);
    model.addAttribute("searchRequest", new SearchRequest());
    return "user-apply-management";
  }

  @GetMapping("/delete-apply")
  public String deleteApply(
      @CookieValue(name = "user", defaultValue = "0") Integer userId,
      @ModelAttribute ApplyId applyId) {
    if (userId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    if (applyService.deleteApply(applyId)) {
      return "redirect:/techJob/user/apply-management/page/1?text=delete-success";
    } else {
      return "redirect:/techJob/user/apply-management/page/1?text=delete-fail";
    }
  }
}
