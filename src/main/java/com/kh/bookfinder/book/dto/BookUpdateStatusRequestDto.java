package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateStatusRequestDto {

  @ValidEnum(enumClass = ApprovalStatus.class, message = Message.INVALID_APPROVAL_STATUS)
  private String approvalStatus;
}