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
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Lob
  @Column(name = "avatar", nullable = false)
  private String avatar;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false, length = 320)
  private String email;

  @Column(name = "password", nullable = false, length = 500)
  private String password;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Column(name = "city", nullable = false)
  private String city;

  @Lob
  @Column(name = "address", nullable = false)
  private String address;

  @Lob
  @Column(name = "introduce")
  private String introduce;

  @Column(name = "verify_code", nullable = false, length = 100)
  private String verifyCode;

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
