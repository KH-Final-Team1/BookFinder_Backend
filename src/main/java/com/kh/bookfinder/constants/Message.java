package com.kh.bookfinder.constants;

public interface Message {

  String SIGNUP_SUCCESS = """
      가입이 완료되었습니다.
      환영합니다.
      로그인 후 이용해 주세요 😊
      """;
  String BAD_REQUEST = "요청이 유효하지 않습니다. 다시 한번 확인해주세요.";
  String VALID_EMAIL = "가입이 가능한 이메일입니다.";
  String INVALID_EMAIL = "유효하지 않은 이메일 형식입니다.";
  String INVALID_PASSWORD = "영문, 숫자, 특수기호를 포함하여 8자 이상 20자 이하로 입력해주세요.";
  String INVALID_PASSWORD_CONFIRM = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
  String INVALID_NICKNAME = "영문, 유효한 한글, 숫자를 이용하여 3자 이상 10자 이하로 입력해주세요.";
  String INVALID_PHONE = "유효하지 않은 휴대폰 번호 형식입니다.";

  String INVALID_ISBN = "유효하지 않은 ISBN 번호 입니다.";
  String INVALID_COST = "금액은 0부터 100000까지의 정수값만 입력 가능합니다.";
  String NULLABLE_COST = "금액은 빈값일 수 없습니다.";
}
