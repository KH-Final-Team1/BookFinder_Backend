package com.kh.bookfinder.user.entity;

import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.user.dto.MyInfoResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
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
  @ManyToOne
  @JoinColumn(name = "BOROUGH_ID")
  private Borough borough;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  private String socialId;
  @Temporal(TemporalType.DATE)
  @Column(updatable = false)
  @CreationTimestamp
  private Date createDate;

  public String extractBoroughName() {
    return this.address.split(" ")[1];
  }

  public MyInfoResponseDto toMyInfoResponse(List<BookTradeListResponseDto> trades) {
    String email = isSocialUser() ? getSocialName() : this.email;
    return MyInfoResponseDto.builder()
        .id(this.id)
        .email(email)
        .phone(this.phone)
        .nickname(this.nickname)
        .borough(this.extractBoroughName())
        .role(this.role)
        .createDate(this.createDate)
        .bookTrades(trades)
        .build();
  }

  private String getSocialName() {
    return this.email.split("@")[1].startsWith("kakao") ? "카카오 로그인 사용자" : "구글 로그인 사용자";
  }

  private boolean isSocialUser() {
    return this.email.split("@")[1].startsWith("kakaoUser") ||
        this.email.split("@")[1].startsWith("googleUser");
  }

  public boolean isAdmin() {
    return this.role.equals(UserRole.ROLE_ADMIN);
  }
}
