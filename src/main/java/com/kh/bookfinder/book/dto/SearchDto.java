package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {

  private String filter;
  @NotNull
  private String keyword;

  @AssertTrue(message = Message.INVALID_FILTER)
  public boolean isFilter() {
    if (filter == null) {
      return false;
    }
    return filter.equals("name")
        || filter.equals("authors")
        || filter.equals("publisher");
  }
}
