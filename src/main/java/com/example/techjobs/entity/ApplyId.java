package com.example.techjobs.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ApplyId implements Serializable {

  private static final long serialVersionUID = 2185919772150372389L;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "job_id", nullable = false)
  private Integer jobId;

  @Override
  public int hashCode() {
    return Objects.hash(jobId, userId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    ApplyId entity = (ApplyId) o;
    return Objects.equals(this.jobId, entity.jobId) && Objects.equals(this.userId, entity.userId);
  }
}
