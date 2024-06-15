package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.validation.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookListRequestDto {

  @ValidEnum(enumClass = BookListFilter.class, message = Message.INVALID_FILTER)
  private String filter;
  @NotNull
  private String keyword;
  @ValidEnum(enumClass = ApprovalStatus.class, nullable = true, message = Message.INVALID_APPROVAL_STATUS)
  private String status;
}