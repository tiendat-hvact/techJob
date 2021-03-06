package com.example.techjobs.controller;

import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.LoginRequest;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.inputDTO.InputUserDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.service.CompanyService;
import com.example.techjobs.service.JobService;
import com.example.techjobs.service.TypeService;
import com.example.techjobs.service.UserService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CommonController {

  private final CompanyService companyService;
  private final UserService userService;
  private final JobService jobService;
  private final TypeService typeService;

  @GetMapping
  public String getHome(
      InputJobDTO job,
      Model model,
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @CookieValue(name = "admin", defaultValue = "1") int adminId,
      @CookieValue(name = "company", defaultValue = "0") int companyId,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "job-not-found":
          notification.setText("Tin tuyển dụng không thể tìm thấy trong hệ thống");
          break;
        case "company-not-found":
          notification.setText("Thông tin công ty không thể tìm thấy trong hệ thống");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    checkCookie(userId, adminId, companyId, model);
    model.addAttribute("companies", companyService.findLimit(6));
    model.addAttribute("currentType", job.getTypeId());
    model.addAttribute("jobs", jobService.findLimit(job, 18));
    model.addAttribute("notification", notification);
    model.addAttribute("types", this.typeService.findAll());
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
        case "not-admin":
          notification.setText("Tài khoản không có quyền quản trị viên");
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
    if ("user".equals(type)) {
      resultLogin = userService.loginAccount(account);
    } else if ("company".equals(type)) {
      resultLogin = companyService.loginAccount(account);
      if (Objects.nonNull(resultLogin)) {
        resultLogin.put("role", "");
      }
    }
    if (resultLogin != null && !resultLogin.isEmpty()) {
      accountId = (Integer) resultLogin.get("accountId");
      if (accountId == 0) {
        type = "admin";
      }

      verify = (Boolean) resultLogin.get("verify");
      if (verify) {
        Cookie cookie = new Cookie(type, String.valueOf(accountId));
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/techJob");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/techJob/" + type;
      }
      return "redirect:/techJob/login?accountId=" + accountId + "&type=" + type + "&verify=false";
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
                cookie.setPath("/techJob");
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
    } else if (type.equals("company") && companyService.checkVerifyCode(accountId, verifyCode)) {
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
        case "invalid-info":
          notification.setText("Thông tin nhập vào không hợp lệ");
          break;
        case "invalid-password":
          notification.setText(
              "Mật khẩu nhập vào không hợp lệ <br> Độ dài bắt buộc của mật khẩu là từ 8 - 20 ký tự");
          break;
        case "password-not-match":
          notification.setText("Nhập lại mật khẩu không khớp");
          break;
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
    if (Utils.isNullOrEmpty(user.getName())
        || Utils.isNullOrEmpty(user.getPhone())
        || Utils.isNullOrEmpty(user.getEmail())
        || Utils.isNullOrEmpty(user.getPassword())) {
      return "redirect:/techJob/user-register?text=invalid-info";
    }
    if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
      return "redirect:/techJob/company-register?text=invalid-password";
    }
    if (!user.getPassword().equals(user.getRePassword())) {
      return "redirect:/techJob/user-register?text=password-not-match";
    }
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
        case "invalid-info":
          notification.setText("Thông tin nhập vào không hợp lệ");
          break;
        case "invalid-password":
          notification.setText(
              "Mật khẩu nhập vào không hợp lệ <br> Độ dài bắt buộc của mật khẩu là từ 8 - 20 ký tự");
          break;
        case "password-not-match":
          notification.setText("Nhập lại mật khẩu không khớp");
          break;
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
    if (Utils.isNullOrEmpty(company.getName())
        || Utils.isNullOrEmpty(company.getEmail())
        || Utils.isNullOrEmpty(company.getPassword())
        || Utils.isNullOrEmpty(company.getPhone())
        || Utils.isNullOrEmpty(company.getCity())
        || Utils.isNullOrEmpty(company.getAddress())) {
      return "redirect:/techJob/company-register?text=invalid-info";
    }
    if (company.getPassword().length() < 8 || company.getPassword().length() > 20) {
      return "redirect:/techJob/company-register?text=invalid-password";
    }
    if (!company.getPassword().equals(company.getRePassword())) {
      return "redirect:/techJob/company-register?text=password-not-match";
    }
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
      @CookieValue(name = "admin", defaultValue = "1") int adminId,
      @CookieValue(name = "company", defaultValue = "0") int companyId,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    OutputJobDTO job = jobService.findById(jobId);
    if (job == null) {
      return "redirect:/techJob?text=job-not-found";
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "apply-success":
          notification.setText(
              "Đăng ký ứng tuyển thành công <br> Xin hãy chờ phía Công ty liên hệ với bạn");
          break;
        case "over-deadline":
          notification.setText("Đã quá thời hạn nộp đơn ứng tuyển");
          break;
        case "cv-none":
          notification.setText(
              "Tài khoản bạn chưa có CV <br> Xin hãy bổ sung CV trước khi ứng tuyển");
          break;
        case "apply-existed":
          notification.setText(
              "Bạn đã nộp đơn ứng tuyển công việc này <br> Xin hãy chờ phía Công ty liên hệ với bạn");
          break;
        case "apply-fail":
          notification.setText("Đăng ký ứng tuyển thất bại");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    checkCookie(userId, adminId, companyId, model);
    model.addAttribute("job", job);
    model.addAttribute("isFollowing", this.jobService.checkFollowing(jobId, userId));
    model.addAttribute("userCookie", userId)
    return "job-info";
  }

  @GetMapping("/introduce-company/{id}")
  public String getCompanyIntroduce(
      Model model,
      @PathVariable(name = "id") int id,
      @CookieValue(name = "user", defaultValue = "0") int userId,
      @CookieValue(name = "admin", defaultValue = "1") int adminId,
      @CookieValue(name = "company", defaultValue = "0") int companyId) {
    OutputCompanyDTO company = companyService.findById(id);
    if (company == null) {
      return "redirect:/techJob?text=company-not-found";
    }
    checkCookie(userId, adminId, companyId, model);
    model.addAttribute("company", company);
    model.addAttribute("isFollowing", this.companyService.checkFollowing(id, userId));
    model.addAttribute("userCookie", userId);
    return "introduce-company";
  }

  protected void checkCookie(int userId, int adminId, int companyId, Model model) {
    if (userId != 0) {
      model.addAttribute("account", userId);
      model.addAttribute("type", "user");
    } else if (companyId != 0) {
      model.addAttribute("account", companyId);
      model.addAttribute("type", "company");
    } else if (adminId != 1) {
      model.addAttribute("account", adminId);
      model.addAttribute("type", "admin");
    } else {
      model.addAttribute("account", 0);
      model.addAttribute("type", null);
    }
  }
}
