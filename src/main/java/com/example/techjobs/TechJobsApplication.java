package com.example.techjobs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.techjobs.common.util.Utils;
import java.util.Properties;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication
public class TechJobsApplication {

  public static void main(String[] args) {
    SpringApplication.run(TechJobsApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public Utils utils() {
    return new Utils();
  }

  @Bean
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setDefaultEncoding("UTF-8");
    return resolver;
  }

  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "dpcd1ks3v",
            "api_key", "772622994145432",
            "api_secret", "la_ZAk_WKk17XFchdKAZGy0DBZ0",
            "secure", true));
  }

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl jMailSender = new JavaMailSenderImpl();
    jMailSender.setHost("smtp-mail.outlook.com");
    jMailSender.setPort(587);
    Properties props = jMailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
    props.put("mail.smtp.ssl.enable", "false");
    props.put("mail.smtp.socketFactory.class", "");
    props.put("mail.smtp.socketFactory.fallback", "false");
    return jMailSender;
  }
}
