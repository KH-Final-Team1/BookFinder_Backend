package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStatusDto {

  private ApprovalStatus approvalStatus;

  @AssertTrue(message = Message.INVALID_APPROVAL_STATUS)
  public boolean isApprovalStatus() {
    if (approvalStatus == null) {
      return false;
    }
    return approvalStatus.equals(ApprovalStatus.APPROVE)
        || approvalStatus.equals(ApprovalStatus.WAIT)
        || approvalStatus.equals(ApprovalStatus.REJECT);
  }

}