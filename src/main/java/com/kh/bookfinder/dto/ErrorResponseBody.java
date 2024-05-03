package com.kh.bookfinder.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseBody {

  private String message;
  @JsonInclude(Include.NON_EMPTY)
  private Map<String, String> details;
}
