package com.example.techjobs.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Lob
  @Column(name = "avatar", nullable = false)
  private String avatar;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "sex", length = 10)
  private String sex;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "email", nullable = false, length = 320)
  private String email;

  @Column(name = "password", nullable = false, length = 500)
  private String password;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Lob
  @Column(name = "address")
  private String address;

  @Column(name = "verify_code", nullable = false, length = 100)
  private String verifyCode;

  @Column(name = "role", nullable = false, length = 20)
  private String role;

  @Column(name = "state", nullable = false, length = 20)
  private String state;

  @Column(name = "create_by", nullable = false)
  private String createBy;

  @Column(name = "create_date", nullable = false)
  private LocalDate createDate;

  @Column(name = "update_by", nullable = false)
  private String updateBy;

  @Column(name = "update_date", nullable = false)
  private LocalDate updateDate;
}
