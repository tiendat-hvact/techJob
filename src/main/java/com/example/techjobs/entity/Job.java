package com.example.techjobs.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "jobs")
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "number_recruit", nullable = false)
  private Integer numberRecruit;

  @Column(name = "gender", length = 10)
  private String gender;

  @Column(name = "salary", nullable = false, length = 100)
  private String salary;

  @Column(name = "`academic level`", nullable = false, length = 100)
  private String academicLevel;

  @Column(name = "experience", nullable = false)
  private Integer experience;

  @Column(name = "working_form", nullable = false, length = 100)
  private String workingForm;

  @Lob
  @Column(name = "welfare", nullable = false)
  private String welfare;

  @Lob
  @Column(name = "description", nullable = false)
  private String description;

  @Lob
  @Column(name = "requirement", nullable = false)
  private String requirement;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id")
  private Type type;

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
