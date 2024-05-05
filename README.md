# API Docs

## Users

### 회원 가입 API (SignUp API)
**Request**
```
POST {BASE_URL}/api/v1/signup

request body (application/json)
{
  "email": "jinho@kh.kr",
  "password": "1@3$ZxcvDs",
  "passwordConfirm": "1@3$ZxcvDs",
  "nickname": "고소하게",
  "address": "서울특별시 강남구 테헤란로 10길",
  "phone": "010-1234-5678",
}
```

**Response**
- 201 Created
```
{
  "message": "회원 가입 성공!\n환영합니다\n.로그인 후 이용해 주세요."
}
```

- 400 Bad Request
```
{
  "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
  "details": {
      "email": "유효하지 않은 이메일 형식입니다.",
      "email": "이미 가입된 이메일입니다.",
      "password": "영문, 숫자, 특수기호를 포함하여 8자 이상 20자 이하로 입력해주세요.",
      "password": "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
      "nickname": "영문, 유효한 한글, 숫자를 이용하여 3자 이상 10자 이하로 입력해주세요.",
      "nickname": "이미 가입된 닉네임입니다.",
      "address": "주소는 서울시로 시작해야 합니다.",
      "phone": "유효하지 않은 휴대폰 번호 형식입니다"
  }
}
```

## BookTrade
### Create BookTrade API
**Request**
```
POST: {BASE_URL}/api/v1/trades

request body (application/json)
{
  "isbn": "9788965402602",
  "tradeType": "rent",
  "rentalCost": "3000",
  "limitedDate": "2024-09-10",
  "content": "책 빌려드립니다",
  "latitude": "37.0",
  "longitude": "127.0"
}
```

**Response**
- 201 Created
```
{
  "message": "정상적으로 등록되었습니다."
}
```

- 400 Bad Request