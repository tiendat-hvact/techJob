package com.example.techjobs.controller;

import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.service.CompanyService;
import com.example.techjobs.service.JobService;
import com.example.techjobs.service.UserService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/techJob")
public class CommonController {

  private final CompanyService companyService;
  private final UserService userService;
  private final JobService jobService;

  @Autowired
  public CommonController(
      CompanyService companyService, UserService userService, JobService jobService) {
    this.companyService = companyService;
    this.userService = userService;
    this.jobService = jobService;
  }

  @GetMapping
  public String getHome(
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @CookieValue(name = "company", defaultValue = "0") int companyId,
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "job-not-found":
          notification.setText("Tin tuyển dụng không thể tìm thấy trong hệ thống");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    if (userId != 0) {
      model.addAttribute("account", userId);
      model.addAttribute("type", "user");
    } else if (companyId != 0) {
      model.addAttribute("account", companyId);
      model.addAttribute("type", "company");
    } else {
      model.addAttribute("account", 0);
      model.addAttribute("type", "");
    }
    model.addAttribute("companies", companyService.findLimit(6));
    model.addAttribute("jobs", jobService.findLimit(18));
    model.addAttribute("notification", notification);
    return "home";
  }

  @GetMapping("/login")
  public String getLoginForm(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @ModelAttribute(name = "accountId") String accountId,
      @ModelAttribute(name = "type") String type,
      @ModelAttribute(name = "verify") String verify) {
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "unauthorized":
          notification.setText("Bạn cần phải đăng nhập trước khi tiếp tục thực thi hành động này");
          break;
        case "login-fail":
          notification.setText(
              "Bạn đã đăng nhập thất bại <br> Xin hãy kiểm tra lại email và mật khẩu");
          break;
        case "account-not-found":
          notification.setText(
              "Tài khoản không thể tìm thấy trong hệ thống <br> Xin hãy đăng ký tài khoản mới");
          break;
        case "create-user-success":
          notification.setText(
              "Bạn đã tạo tài khoản thành công <br> Xin hãy vào Email để lấy mã kích hoạt và đăng nhập vào hệ thống");
          break;
        case "create-company-success":
          notification.setText(
              "Công ty đã tạo tài khoản thành công <br> Xin hãy vào Email để lấy mã kích hoạt và đăng nhập vào hệ thống");
          break;
        case "check-verifyCode-fail":
          notification.setText(
              "Bạn đã xác thực tài khoản thất bại <br> Xin hãy vào Email để lấy mã kích hoạt và đăng nhập vào hệ thống");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("account", new LoginRequest());
    model.addAttribute("notification", notification);
    model.addAttribute("accountId", accountId);
    model.addAttribute("verify", verify);
    return "login";
  }

  @PostMapping("/login-account")
  public String loginAccount(
      HttpServletResponse response, @ModelAttribute(name = "user") LoginRequest account) {
    int accountId;
    Boolean verify;
    String type = account.getType();
    Map<String, Object> resultLogin = new HashMap<>();
    if (type.equals("user")) {
      resultLogin = userService.loginAccount(account);
    } else if (type.equals("company")) {
      resultLogin = companyService.loginAccount(account);
    }
    if (resultLogin != null && !resultLogin.isEmpty()) {
      accountId = (Integer) resultLogin.get("accountId");
      verify = (Boolean) resultLogin.get("verify");
      if (verify) {
        Cookie cookie = new Cookie(type, String.valueOf(accountId));
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/techJob");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/techJob/" + type;
      } else {
        return "redirect:/techJob/login?accountId=" + accountId + "&type=" + type + "&verify=false";
      }
    }
    return "redirect:/techJob/login?text=login-fail";
  }

  @GetMapping("/logout")
  public String logout(HttpServletResponse response, HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .forEach(
              cookie -> {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
              });
    }
    return "redirect:/techJob/login";
  }

  @GetMapping("/check-verifyCode/{type}/{accountId}")
  public String checkVerify(
      HttpServletResponse response,
      @PathVariable(name = "type") String type,
      @PathVariable(name = "accountId") int accountId,
      @ModelAttribute(name = "verifyCode") String verifyCode) {
    if (type.equals("user") && userService.checkVerifyCode(accountId, verifyCode)) {
      Cookie cookie = new Cookie("user", String.valueOf(accountId));
      cookie.setMaxAge(24 * 60 * 60);
      cookie.setPath("/techJob");
      cookie.setHttpOnly(true);
      response.addCookie(cookie);
      return "redirect:/techJob/user";
    }
    if (type.equals("company") && companyService.checkVerifyCode(accountId, verifyCode)) {
      Cookie cookie = new Cookie("company", String.valueOf(accountId));
      cookie.setMaxAge(24 * 60 * 60);
      cookie.setPath("/techJob");
      cookie.setHttpOnly(true);
      response.addCookie(cookie);
      return "redirect:/techJob/company";
    }
    return "redirect:/techJob/login?text=check-verifyCode-fail";
  }

  @GetMapping("/user-register")
  public String getUserRegisterForm(
      Model model, @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "create-user-fail":
          notification.setText(
              "Email của bạn đã có trong hệ thống <br> Bạn có thể yêu cầu lấy lại mật khẩu trong trường hợp bị quên");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("notification", notification);
    model.addAttribute("user", new InputUserDTO());
    return "user-register";
  }

  @PostMapping("/create-user")
  public String createUser(@ModelAttribute(name = "user") InputUserDTO user) {
    if (userService.createUser(user)) {
      return "redirect:/techJob/login?text=create-user-success";
    } else {
      return "redirect:/techJob/user-register?text=create-user-fail";
    }
  }

  @GetMapping("/company-register")
  public String getCompanyRegisterForm(
      Model model, @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "create-company-fail":
          notification.setText(
              "Email của công ty đã có trong hệ thống <br> Công ty có thể yêu cầu lấy lại mật khẩu trong trường hợp bị quên");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("notification", notification);
    model.addAttribute("company", new InputCompanyDTO());
    return "company-register";
  }

  @PostMapping("/create-company")
  public String createCompany(@ModelAttribute(name = "company") InputCompanyDTO company) {
    if (companyService.createCompany(company)) {
      return "redirect:/techJob/login?text=create-company-success";
    } else {
      return "redirect:/techJob/company-register?text=create-company-fail";
    }
  }

  @GetMapping("/job/{id}")
  public String getJobInfo(
      Model model,
      @PathVariable(name = "id") int jobId,
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @CookieValue(name = "company", defaultValue = "0") int companyId,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    OutputJobDTO job = jobService.findById(jobId);
    if (job == null) {
      return "redirect:/?text=job-not-found";
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "apply-success":
          notification.setText(
              "Đăng ký ứng tuyển thành công <br> Xin hãy chờ phía Công ty liên hệ với bạn");
          break;
        case "apply-fail":
          notification.setText(
              "Đăng ký ứng tuyển thất bại <br> Xin hãy kiểm tra lại tài khoản của bạn đã có CV chưa ?");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    if (userId != 0) {
      model.addAttribute("account", userId);
      model.addAttribute("type", "user");
    } else if (companyId != 0) {
      model.addAttribute("account", companyId);
      model.addAttribute("type", "company");
    } else {
      model.addAttribute("account", 0);
      model.addAttribute("type", "");
    }
    model.addAttribute("job", job);
    return "job-info";
  }
}
