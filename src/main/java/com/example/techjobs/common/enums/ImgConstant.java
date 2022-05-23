package com.example.techjobs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImgConstant {
  Prefix("https://res.cloudinary.com/dpcd1ks3v/image/upload/"),
  UnknownUser("https://res.cloudinary.com/dpcd1ks3v/image/upload/v1653189947/user/unknow_user.jpg"),
  UnknownJob(
      "https://res.cloudinary.com/dralhpdiv/image/upload/c_thumb,w_200,g_face/v1643096968/job/unknow_job.png"),
  UnknownCompany(
      "https://res.cloudinary.com/dpcd1ks3v/image/upload/v1653234811/company/unknow_company.png"),
  UnknownNews(
      "https://res.cloudinary.com/dralhpdiv/image/upload/c_scale,h_162,w_260/v1646968828/news/unknow_news.jpg");
  private String value;
}
