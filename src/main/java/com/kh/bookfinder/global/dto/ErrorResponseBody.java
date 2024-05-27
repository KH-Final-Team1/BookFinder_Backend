package com.kh.bookfinder.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kh.bookfinder.global.constants.HttpErrorMessage;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseBody {

  private String message;
  @JsonInclude(Include.NON_EMPTY)
  private String detail;
  @JsonInclude(Include.NON_EMPTY)
  private Map<String, String> details;

  public static class ErrorResponseBodyBuilder {

    public ErrorResponseBodyBuilder message(HttpErrorMessage errorStatusCode) {
      this.message = errorStatusCode.getMessage();
      return this;
    }
  }
}
