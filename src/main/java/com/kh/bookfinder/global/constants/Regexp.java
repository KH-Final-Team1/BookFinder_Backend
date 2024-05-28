package com.kh.bookfinder.global.constants;

public interface Regexp {

  String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  String PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_=+]).{8,20}$";
  String NICKNAME = "^[가-힣a-zA-Z0-9]{3,20}$";
  String PHONE = "^01[0-9][0-9]{3,4}[0-9]{4}$";
  String AUTH_CODE = "^[a-zA-Z0-9]{8}$";
  String ADDRESS = "^서울(시)? [가-힣]+구 .+$";
}
