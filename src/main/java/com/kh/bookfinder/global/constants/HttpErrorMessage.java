package com.kh.bookfinder.global.constants;

import lombok.Getter;

@Getter
public enum HttpErrorMessage {
  BAD_REQUEST("요청이 유효하지 않습니다. 다시 한번 확인해주세요."),
  UNAUTHORIZED("인증 정보가 유효하지 않습니다."),
  FORBIDDEN("잘못된 권한입니다."),
  NOT_FOUND("요청하신 정보를 찾을 수 없습니다."),
  CONFLICT("이미 존재하는 리소스입니다."),
  UNSUPPORTED_MEDIA_TYPE("유효하지 않은 Content-Type 입니다.");

  private final String message;

  HttpErrorMessage(String message) {
    this.message = message;
  }
}
