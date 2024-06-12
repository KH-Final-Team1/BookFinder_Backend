![Untitled](https://github.com/KH-Final-Team1/BookFinder_Frontend/assets/45187382/f2fc6c74-ec83-4727-b521-eb522ee3f928)

# 💡 Topic

- 서울시 공공 도서관 소장자료, 한번에 검색하자! 북적북적

# 📝 Service Summary

1-1. 개발 서적을 참고하여 공부할 때

- 책 전체를 다 보는 경우도 있지만 부분만 참고하는 경우도 많다
- 표지만 보고 구입했지만 원하는 내용이 없는 경우가 있다

1-2. 무작정 책을 구매하기 전에 대여해서 보고 만족도가 높을 때 구입하는 것이 좋다

2-1. 서울시 내에 있는 공공 도서관들이 많은 개발 서적을 소장하고 있다.

2-2. 서울 시민이라면 서울 내 어느 도서관에서도 책을 대여할 수 있다.

2-3. 그러나 소장 자료를 검색하기 위해서는 자치구 도서관 페이지를 확인해야하는 단점이 있다.

![Untitled](https://github.com/KH-Final-Team1/BookFinder_Frontend/assets/45187382/93645a42-667d-408a-acaf-21e6344aa604)

**한 번의 도서 검색으로 소장 중인 서울 공공 도서관을 보여주고 대출 가능 여부를 알려주는 서비스**

![1](https://github.com/KH-Final-Team1/BookFinder_Frontend/assets/45187382/05c966d5-806b-418e-bda2-4ac4460a7f48)

![2](https://github.com/KH-Final-Team1/BookFinder_Frontend/assets/45187382/12c608c0-c582-49fc-aae0-29a6ab805852)

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

---

## BookTrade

### Create BookTrade API

**Request**

```
POST: {BASE_URL}/api/v1/trades

request body (application/json)
{
  "isbn": "9788965402602",
  "tradeType": "LEND",
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

### List BookTrade API

**Request**

```
GET: {BASE_URL}/api/v1/trades/list/{boroughId}
```

**Response**

- 200 Ok
    - 데이터가 있는 경우
        ```
        {
          [
            {
                "id": 1,
                "tradeType": "BORROW",
                "tradeYn": "N",
                "rentalCost": 10000,
                "createdDate": "2024-05-02",
                "user": {
                    "nickname": "testNick1"
                }
                "book": {
                    "name": "test book name1",
                    "authors": "test book authors1 etc..",
                    "publicationYear": 2024
                    "imageUrl": "test url1"
                }
                "borough": {
                    "name": "강남구"
                }
            },
                {
                "id": 2,
                "tradeType": "LEND",
                "tradeYn": "N",
                "rentalCost": 13000,
                "createdDate": "2024-05-02",
                "user": {
                    "nickname": "testNick2"
                }
                "book": {
                    "name": "test book name2",
                    "authors": "test book authors2 etc..",
                    "publicationYear": 2024
                    "imageUrl": "test url2"
                }
                "borough": {
                    "name": "강남구"
                }
            },
                {
                "id": 1,
                "tradeType": "BORROW",
                "tradeYn": "Y",
                "rentalCost": 5000,
                "createdDate": "2024-05-02",
                "user": {
                    "nickname": "testNick1"
                }
                "book": {
                    "name": "test book name3",
                    "authors": "test book authors3 etc..",
                    "publicationYear": 2024
                    "imageUrl": "test url3"
                }
                "borough": {
                    "name": "강남구"
                }
            },
            ...
          ]
        }
        ```
    - 데이터가 없는 경우
        ```
            []
        ```

- 400 Bad Request
    ```
    {
        "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        "details": {
            "boroughId": "지역 번호는 1부터 25까지의 숫자만 입력 가능합니다."          
        }
    }
    ```

### Get BookTrade API

**Request**

```
GET: {BASE_URL}/api/v1/trades/{tradeId}
```

**Response**

- 200 Ok
    ```
    {
        "id": 1,
        "tradeType": "BORROW",
        "tradeYn": "N",
        "content": "책 빌려주세요.",
        "rentalCost": 10000,
        "longitude": 18.12342,
        "latitude": 19.12115,
        "createdDate": "2024-05-15"
        "book": {
            "name": "test book name1",
            "authors": "test book authors1 etc..",
            "publisher": "test publisher1",
            "publicationYear": 2024
            "imageUrl": "test url3"
            "description": "test description"
        }
        "user": {
            "nickname": "고소하게"
        }
    }
    ```

- 404 Not Found
    - boroughId에 대한 튜플이 DB에 없는 경우
      ```
      {
          "message": "거래 번호를 찾을 수 없습니다."
      }
      ```
    - boroughId에 대한 튜플의 상태가 Delete인 경우
      ```
      {
          "message": "삭제된 게시물 입니다."
      }
      ```
