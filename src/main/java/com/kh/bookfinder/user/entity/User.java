package com.kh.bookfinder.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  @Column(name = "USER_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  private String phone;
  @Column(unique = true, nullable = false)
  private String nickname;
  private String address;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  private String socialId;
  @Temporal(TemporalType.DATE)
  @Column(updatable = false)
  @CreationTimestamp
  private Date createDate;

}
