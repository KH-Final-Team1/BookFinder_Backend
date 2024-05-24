package com.kh.bookfinder.comment.entity;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.comment.dto.CommentResponseDto;
import com.kh.bookfinder.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

  @Id
  @Column(name = "COMMENT_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "TRADE_ID")
  private BookTrade bookTrade;
  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;
  private String content;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status secretYn;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status deleteYn = Status.N;
  @CreationTimestamp
  private Date createDate;
  @UpdateTimestamp
  private Date updateDate;

  public <T> T toResponse(Class<T> responseType) {
    return (T) this.toResponseDto();
  }

  private CommentResponseDto toResponseDto() {
    return CommentResponseDto.builder()
        .id(this.id)
        .content(this.content)
        .secretYn(this.secretYn)
        .createDate(this.createDate)
        .userId(this.user.getId())
        .nickname(this.user.getNickname())
        .build();
  }
}
