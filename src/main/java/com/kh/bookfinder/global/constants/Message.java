package com.kh.bookfinder.global.constants;

public interface Message {

  String SIGNUP_SUCCESS = """
      가입이 완료되었습니다.
      환영합니다.
      로그인 후 이용해 주세요 😊
      """;
  String BAD_REQUEST = "요청이 유효하지 않습니다. 다시 한번 확인해주세요.";
  String NOT_FOUND = "요청하신 정보를 찾을 수 없습니다.";
  String UNAUTHORIZED = "인증 정보가 유효하지 않습니다.";


  String VALID_EMAIL = "가입이 가능한 이메일입니다.\n사용하시겠습니까?";
  String INVALID_EMAIL = "유효하지 않은 이메일 형식입니다.";
  String MAIL_SUBJECT = "북적북적에서 보내는 이메일 인증 코드입니다.";
  String DUPLICATE_EMAIL = "이미 가입된 이메일입니다.";
  String INVALID_FIELD = "field는 email이나 nickname만 가능합니다.";
  String INVALID_PASSWORD = "영문, 숫자, 특수기호를 포함하여 8자 이상 20자 이하로 입력해주세요.";
  String INVALID_PASSWORD_CONFIRM = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
  String INVALID_NICKNAME = "영문, 유효한 한글, 숫자를 이용하여 3자 이상 20자 이하로 입력해주세요.";
  String VALID_NICKNAME = "가입이 가능한 닉네임입니다.\n사용하시겠습니까?";
  String DUPLICATE_NICKNAME = "이미 가입된 닉네임입니다.";
  String INVALID_PHONE = "유효하지 않은 휴대폰 번호 형식입니다.";
  String INVALID_AUTH_CODE = "유효하지 않은 인증 코드입니다.";
  String EXPIRED_AUTH_CODE = "인증 코드가 만료되었습니다.";
  String INVALID_SIGNING_TOKEN = "유효하지 않은 토큰입니다.";
  String FAIL_LOGIN = "이메일 또는 비밀번호가 올바르지 않습니다.";


  String INVALID_ISBN_DIGITS = "ISBN 번호는 13자리의 숫자만 입력 가능합니다.";
  String UNSAVED_ISBN = "유효하지 않은 ISBN 번호 입니다.";
  String INVALID_COST = "금액은 0부터 100000까지의 정수값만 입력 가능합니다.";
  String NULLABLE_COST = "금액은 빈값일 수 없습니다.";
  String INVALID_BOROUGH = "지역 번호는 1부터 25까지의 숫자만 입력 가능합니다.";
  String NOT_FOUND_TRADE = "거래 번호를 찾을 수 없습니다.";
  String DELETED_TRADE = "삭제된 게시물 입니다.";
  String SUCCESS_UPDATE = "게시글을 성공적으로 수정했습니다.";
  String SUCCESS_DELETE = "게시글을 성공적으로 삭제했습니다.";
  String SUCCESS_CHANGE = "거래 상태가 변경되었습니다.";

  String INVALID_FILTER = "filter는 name이나 authors나 publisher만 가능합니다.";
  String NOT_FOUND_BOOK = "해당 도서는 북적북적 사이트에 없는 도서이거나 존재하지 않는 도서입니다.";
  String UPDATE_APPROVAL_STATUS = "승인 여부가 변경되었습니다.";
  String INVALID_APPROVAL_STATUS = "approvalStatus는 APPROVE나 WAIT나 REJECT만 가능합니다";


  static String getSuccessMessageBy(String field) {
    return field.equals("email") ? VALID_EMAIL : VALID_NICKNAME;
  }
}
