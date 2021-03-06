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
          notification.setText("Tin tuy???n d???ng kh??ng th??? t??m th???y trong h??? th???ng");
          break;
        case "company-not-found":
          notification.setText("Th??ng tin c??ng ty kh??ng th??? t??m th???y trong h??? th???ng");
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
          notification.setText("B???n c???n ph???i ????ng nh???p tr?????c khi ti???p t???c th???c thi h??nh ?????ng n??y");
          break;
        case "login-fail":
          notification.setText(
              "B???n ???? ????ng nh???p th???t b???i <br> Xin h??y ki???m tra l???i email v?? m???t kh???u");
          break;
        case "account-not-found":
          notification.setText(
              "T??i kho???n kh??ng th??? t??m th???y trong h??? th???ng <br> Xin h??y ????ng k?? t??i kho???n m???i");
          break;
        case "create-user-success":
          notification.setText(
              "B???n ???? t???o t??i kho???n th??nh c??ng <br> Xin h??y v??o Email ????? l???y m?? k??ch ho???t v?? ????ng nh???p v??o h??? th???ng");
          break;
        case "create-company-success":
          notification.setText(
              "C??ng ty ???? t???o t??i kho???n th??nh c??ng <br> Xin h??y v??o Email ????? l???y m?? k??ch ho???t v?? ????ng nh???p v??o h??? th???ng");
          break;
        case "check-verifyCode-fail":
          notification.setText(
              "B???n ???? x??c th???c t??i kho???n th???t b???i <br> Xin h??y v??o Email ????? l???y m?? k??ch ho???t v?? ????ng nh???p v??o h??? th???ng");
          break;
        case "not-admin":
          notification.setText("T??i kho???n kh??ng c?? quy???n qu???n tr??? vi??n");
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
          notification.setText("Th??ng tin nh???p v??o kh??ng h???p l???");
          break;
        case "invalid-password":
          notification.setText(
              "M???t kh???u nh???p v??o kh??ng h???p l??? <br> ????? d??i b???t bu???c c???a m???t kh???u l?? t??? 8 - 20 k?? t???");
          break;
        case "password-not-match":
          notification.setText("Nh???p l???i m???t kh???u kh??ng kh???p");
          break;
        case "create-user-fail":
          notification.setText(
              "Email c???a b???n ???? c?? trong h??? th???ng <br> B???n c?? th??? y??u c???u l???y l???i m???t kh???u trong tr?????ng h???p b??? qu??n");
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
          notification.setText("Th??ng tin nh???p v??o kh??ng h???p l???");
          break;
        case "invalid-password":
          notification.setText(
              "M???t kh???u nh???p v??o kh??ng h???p l??? <br> ????? d??i b???t bu???c c???a m???t kh???u l?? t??? 8 - 20 k?? t???");
          break;
        case "password-not-match":
          notification.setText("Nh???p l???i m???t kh???u kh??ng kh???p");
          break;
        case "create-company-fail":
          notification.setText(
              "Email c???a c??ng ty ???? c?? trong h??? th???ng <br> C??ng ty c?? th??? y??u c???u l???y l???i m???t kh???u trong tr?????ng h???p b??? qu??n");
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
              "????ng k?? ???ng tuy???n th??nh c??ng <br> Xin h??y ch??? ph??a C??ng ty li??n h??? v???i b???n");
          break;
        case "over-deadline":
          notification.setText("???? qu?? th???i h???n n???p ????n ???ng tuy???n");
          break;
        case "cv-none":
          notification.setText(
              "T??i kho???n b???n ch??a c?? CV <br> Xin h??y b??? sung CV tr?????c khi ???ng tuy???n");
          break;
        case "apply-existed":
          notification.setText(
              "B???n ???? n???p ????n ???ng tuy???n c??ng vi???c n??y <br> Xin h??y ch??? ph??a C??ng ty li??n h??? v???i b???n");
          break;
        case "apply-fail":
          notification.setText("????ng k?? ???ng tuy???n th???t b???i");
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
