package com.kh.bookfinder.global.constants;

public interface Message {

  String SIGNUP_SUCCESS = """
      가입이 완료되었습니다.
      환영합니다.
      로그인 후 이용해 주세요 😊
      """;

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
  String INVALID_ADDRESS = "유효하지 않은 주소 형식입니다. 현재는 서울시만 등록 가능합니다.";
  String INVALID_PHONE = "유효하지 않은 휴대폰 번호 형식입니다.";
  String INVALID_AUTH_CODE = "유효하지 않은 인증 코드입니다.";
  String EXPIRED_AUTH_CODE = "인증 코드가 만료되었습니다.";
  String INVALID_SIGNING_TOKEN = "유효하지 않은 Signing 토큰입니다.";
  String NOT_FOUND_EMAIL_AUTH = "인증 코드에 해당하는 이메일을 찾을 수 없습니다. 다시 한번 확인해주세요.";

  String FAIL_LOGIN = "이메일 또는 비밀번호가 올바르지 않습니다.";
  String INVALID_LOGIN_WITH_AUTHORIZATION = "헤더에 Authorization이 있어 로그인 요청을 할 수 없습니다.";
  String ALREADY_LOGIN = "이미 로그인 되어 있습니다.";
  String NOT_LOGIN = "회원 정보가 없습니다. 로그인 후 이용해주세요";
  String NOT_FOUND_USER = "요청하신 사용자를 찾을 수 없습니다.";

  String INVALID_JWT_SIGNATURE = "잘못된 JWT 서명입니다.";
  String INVALID_ACCESS_TOKEN = "유효하지 않은 토큰입니다.";
  String EXPIRED_ACCESS_TOKEN = "만료된 토큰입니다.";

  String INVALID_ISBN_DIGITS = "ISBN 번호는 13자리의 숫자만 입력 가능합니다.";
  String UNSAVED_ISBN = "유효하지 않은 ISBN 번호 입니다.";
  String INVALID_COST = "금액은 0부터 100000까지의 정수값만 입력 가능합니다.";
  String INVALID_LIMITED_DATE = "오늘 날짜 이후로 설정해주세요.";
  String INVALID_BOROUGH = "지역 번호는 1부터 25까지의 숫자만 입력 가능합니다.";
  String NOT_FOUND_TRADE = "거래 번호를 찾을 수 없습니다.";
  String DELETED_TRADE = "삭제된 게시물 입니다.";
  String SUCCESS_CHANGE = "거래 상태가 변경되었습니다.";
  String SUCCESS_UPDATE = "수정을 성공했습니다.";
  String SUCCESS_DELETE = "삭제를 성공했습니다.";
  String FORBIDDEN_BOOK_TRADES_READ = "자신의 자치구 게시판/게시글만 조회할 수 있습니다.";
  String FORBIDDEN_BOOK_TRADES_UPDATE = "자신의 게시글만 수정/삭제할 수 있습니다.";

  String INVALID_FILTER = "filter는 name이나 authors나 publisher만 가능합니다.";
  String NOT_FOUND_BOOK = "해당 도서는 북적북적 사이트에 없는 도서이거나 존재하지 않는 도서입니다.";
  String UPDATE_APPROVAL_STATUS = "승인 여부가 변경되었습니다.";
  String INVALID_APPROVAL_STATUS = "approvalStatus는 APPROVE나 WAIT나 REJECT만 가능합니다";
  String DUPLICATE_BOOK_APPROVE = "이미 등록되어 있는 도서입니다.";
  String DUPLICATE_BOOK_WAIT = "이미 요청되어 있는 도서입니다.";
  String DUPLICATE_BOOK_REJECT = "해당 도서는 요청이 거절되어 있는 도서 입니다.";
  String SUCCESS_REQUEST = "도서가 성공적으로 요청되었습니다.";

  String NOT_FOUND_COMMENT = "존재하지 않는 댓글 입니다.";
  String CONTENT_REQUIRED = "내용은 빈값일 수 없습니다.";
  String SECRET_REQUIRED = "비밀댓글 여부를 설정해주세요.";

  String NOT_AUTHORIZED = "해당 기능에 대한 권한이 없습니다.";

  static String getSuccessMessageBy(String field) {
    return field.equals("email") ? VALID_EMAIL : VALID_NICKNAME;
  }
}