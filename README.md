# Notion 링크
- https://tin-digit-d17.notion.site/Backend-README-md-fbf898f7551e4da582369aa3790a7946

------

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

# 🌟 API Docs

## Users / Auth

### SignUp API

- **Request**
    - url
        
        ```java
        POST {BASE_URL}/api/v1/signup
        ```
        
    - request
        
        ```
        {
          "email": "jinho@kh.kr",
          "password": "1@3$ZxcvDs",
          "passwordConfirm": "1@3$ZxcvDs",
          "nickname": "고소하게",
          "address": "서울특별시 강남구 테헤란로 10길",
          "phone": "01012345678",
        }
        ```
        
- **Response**
    - 201 Created
        
        ```
        {
          "message": "가입이 완료되었습니다.\\n환영합니다\\n.로그인 후 이용해 주세요 😊"
        }
        ```
        
    - 400 Bad Request
        1. 유효하지 않은 이메일 형식
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
                  "email": "유효하지 않은 이메일 형식입니다.",
              }
            }
            ```
            
        2. 유효하지 않은 비밀번호 형식
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
                  "password": "영문, 숫자, 특수기호를 포함하여 8자 이상 20자 이하로 입력해주세요.",
              }
            }
            ```
            
        3. 비밀번호와 비밀번호 확인 불일치
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
                  "passwordConfirm": "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
              }
            }
            ```
            
        4. 유효하지 않은 닉네임 형식
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
            		"nickname": "영문, 유효한 한글, 숫자를 이용하여 3자 이상 10자 이하로 입력해주세요.",
              }
            }
            ```
            
        5. 유효하지 않은 주소 형식
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
                  "address": "유효하지 않은 주소 형식입니다. 현재는 서울시만 등록 가능합니다.",
              }
            }
            ```
            
        6. 유효하지 않은 휴대폰 번호 형식
            
            ```
            {
              "message": "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."
              "details": {
                  "phone": "유효하지 않은 휴대폰 번호 형식입니다"
              }
            }
            ```
            
    - 403 Forbidden
        
        ```
        {
          "message": "잘못된 권한입니다."
          "detail": "이미 로그인 되어 있습니다."
        }
        ```
        
    - 409 Confilct
        1. 이미 존재하는 이메일
            
            ```
            {
              "message": "이미 존재하는 리소스입니다."
              "detail": "이미 가입된 이메일입니다."
            }
            ```
            
        2. 이미 존재하는 닉네임
            
            ```
            {
              "message": "이미 존재하는 리소스입니다."
              "detail": "이미 가입된 닉네임입니다."
            }
            ```
            

### Duplicate Check API

- **Request**
    - url
        
        ```
        GET {BASE_URL}/api/v1/signup/duplicate
        ```
        
    - request
        
        ```
        GET {BASE_URL}/api/v1/signup/duplicate?field=email&value=jinho@kh.kr
        ```
        
        - field: email/nickname만 가능, 필수
        - value: String, 필수
- **Response**
    - 200 Ok
        1. field가 nickname이고 유효한 value
            
            ```
            {
            	"message": "가입이 가능한 이메일입니다.\n사용하시겠습니까?"
            }
            ```
            
        2. field가 email이고 유효한 value
            
            ```
            {
            	"message": "가입이 가능한 닉네임입니다.\n사용하시겠습니까?";
            }
            ```
            
    - 400 Bad Request
        1. field가 email이고 value가 유효하지 않은 email 형식
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"email": "유효하지 않은 이메일 형식입니다."
            	}
            }
            ```
            
        2. field가 nickname이고 value가 유효하지 않은 nickname 형식
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"nickname": "영문, 유효한 한글, 숫자를 이용하여 3자 이상 20자 이하로 입력해주세요."
            	}
            }
            ```
            
        3. field가 email이나 nickname이 아님
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"field": "field는 email이나 nickname만 가능합니다."
            	}
            }
            ```
            
    - 403 Forbidden
        
        ```
        {
          "message": "잘못된 권한입니다."
          "detail": "이미 로그인 되어 있습니다."
        }
        ```
        
    - 409 Conflict
        1. field가 email이고 value가 이미 존재하는 email
            
            ```
            {
              "message": "이미 존재하는 리소스입니다."
              "detail": "이미 가입된 이메일입니다."
            }
            ```
            
        2. field가 nickname이고 value가 이미 존재하는 nickname
            
            ```
            {
              "message": "이미 존재하는 리소스입니다."
              "detail": "이미 가입된 닉네임입니다."
            }
            ```
            

### Send Auth Email API

- **Request**
    - url
        
        ```
        POST {BASE_URL}/api/v1/signup/email
        ```
        
    - request
        
        ```
        {
        	"email": "jinho@kh.kr"
        }
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
        	"signingToken": "eyvalruwerdfs~~~"
        }
        ```
        
    - 400 Bad Request
        - 유효하지 않은 이메일 형식
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"email": "유효하지 않은 이메일 형식입니다."
            	}
            }
            ```
            
    - 403 Forbidden
        
        ```
        {
          "message": "잘못된 권한입니다."
          "detail": "이미 로그인 되어 있습니다."
        }
        ```
        
    - 409 Conflict
        1. 이미 존재하는 이메일
            
            ```
            {
              "message": "이미 존재하는 리소스입니다."
              "detail": "이미 가입된 이메일입니다."
            }
            ```
            

### Check Verification AuthCode API

- **Request**
    - url
        
        ```
        POST {BASE_URL}/api/v1/signup/verification-code
        ```
        
    - request
        
        ```
        {
        	"authCode": "Qws24dEc",
        	"signingToken": "evawerhqu~~"
        }
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
        	"signingToken": "eyvalruwerdfs~~~"
        }
        ```
        
    - 400 Bad Request
        1. 유효하지 않은 authCode
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"authCode": "유효하지 않은 인증 코드입니다."
            	}
            }
            ```
            
        2. 유효하지 않은 signingToken
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"siginingToken": "유효하지 않은 Signing 토큰입니다."
            	}
            }
            ```
            
        3. 만료된 authCode
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
            	"details": {
            		"authCode": "인증 코드가 만료되었습니다."
            	}
            }
            ```
            
    - 403 Forbidden
        
        ```
        {
          "message": "잘못된 권한입니다."
          "detail": "이미 로그인 되어 있습니다."
        }
        ```
        
    - 404 Not Found
        
        ```
        {
        	"message": "요청하신 정보를 찾을 수 없습니다.",
        	"detail": "인증 코드에 해당하는 이메일을 찾을 수 없습니다. 다시 한번 확인해주세요."
        }
        ```
        

### Get My User Info API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        GET {BASE_URL}/api/v1/users/my-info
        ```
        
    - request
        
        ```
        GET {BASE_URL}/api/v1/users/my-info
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
        	"id": 1,
        	"email": "jinho@kh.kr",
        	"phone": "01012345678",
        	"nickname": "고소하게",
        	"borough": "관악구",
        	"role": "ROLE_ADMIN",
        	"createDate": "2024-05-11"
        	"bookTrades": [
        		{
              "id": 1,
              "tradeType": "BORROW",
              "tradeYn": "Y",
              "deleteYn": "N",
              "rentalCost": 10000,
              "createDate": "2024-06-12",
              "book": {
                "imageUrl": "test book image url",
                "name": "test book name",
                "publicationYear": 2024,
                "authors": "test book authors"
              },
              "user": {
                "nickname": "고소하게"
              },
              "borough": {
                "name": "관악구"
              }
            },
            {
              "id": 2,
              "tradeType": "BORROW",
              "tradeYn": "Y",
              "deleteYn": "N",
              "rentalCost": 10000,
              "createDate": "2024-06-12",
              "book": {
                "imageUrl": "test book image url",
                "name": "test book name",
                "publicationYear": 2024,
                "authors": "test book authors"
              },
              "user": {
                "nickname": "고소하게"
              },
              "borough": {
                "name": "관악구"
              }
            },
            ...
        	]
        }
        ```
        
    - 401 Unauthorized
        
        ```
        {
          "message": "인증 정보가 유효하지 않습니다."
          "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
        }
        ```
        

### Login

- **Request**
    - url
        
        ```
        POST {BASE_URL}/api/v1/login
        ```
        
    - request
        
        ```
        {
        	"email": "jinho@kh.kr",
        	"password": "wur23984hwer21"
        }
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
        	"accessToken" : "eyaewerqkjjsand~~~"
        } 
        ```
        
    - 400 Bad Request
        1. email 형식이 유효하지 않음
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            	"details": {
            		"email": "유효하지 않은 이메일 형식입니다."
            	}
            }
            ```
            
        2. password 형식이 유효하지 않음
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            	"details": {
            		"passowrd": "영문, 숫자, 특수기호를 포함하여 8자 이상 20자 이하로 입력해주세요."
            	}
            }
            ```
            
    - 401 Unauthorized
        1. 이메일/비밀번호 틀림
            
            ```
            {
              "message": "인증 정보가 유효하지 않습니다."
              "detail": "이메일 또는 비밀번호가 올바르지 않습니다."
            }
            ```
            
        2. 이미 로그인 되어 있음
            
            ```
            {
              "message": "인증 정보가 유효하지 않습니다."
              "detail": "헤더에 Authorization이 있어 로그인 요청을 할 수 없습니다."
            }
            ```
            

---

## Book

### List Book API

- **Request**
    - url
        
        ```
        GET {BASE_URL}/api/v1/books/list
        ```
        
    - request
        
        ```
        GET {BASE_URL}/api/v1/books/list?filter=name&keyword=자바&status=approve
        ```
        
        - filter: name, authors, publisher만 가능, case ignore, 필수 O
        - keyword: string
        - status: approve, wait, reject만 가능, case ignore 필수 X
- **Response**
    - 200 Ok
        
        ```
        [
          {
            "isbn": 1234567891011,
            "additionSymbol": null,
            "name": "test book name 0",
            "authors": "test book authors 0",
            "publisher": "test book publisher 0",
            "publicationYear": 2024,
            "classNo": null,
            "className": null,
            "description": "test book description 0",
            "imageUrl": "test book image url 0",
            "approvalStatus": "APPROVE"
          },
          {
            "isbn": 1234567891012,
            "additionSymbol": null,
            "name": "test book name 1",
            "authors": "test book authors 1",
            "publisher": "test book publisher 1",
            "publicationYear": 2024,
            "classNo": null,
            "className": null,
            "description": "test book description 1",
            "imageUrl": "test book image url 1",
            "approvalStatus": "APPROVE"
          },
          {
            "isbn": 1234567891013,
            "additionSymbol": null,
            "name": "test book name 2",
            "authors": "test book authors 2",
            "publisher": "test book publisher 2",
            "publicationYear": 2024,
            "classNo": null,
            "className": null,
            "description": "test book description 2",
            "imageUrl": "test book image url 2",
            "approvalStatus": "APPROVE"
          },
          ...
        ]
        ```
        
    - 400 Bad Request
        1. filter가 유효하지 않음
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            	"details": {
            		"filter": "filter는 name, authors, publisher만 가능합니다."
            	}
            }
            ```
            
        2. status가 유효하지 않음
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            	"details": {
            		"status": "status는 approve, wait, reject만 가능합니다"
            	}
            }
            ```
            

### Detail Book API

- **Request**
    - url
        
        ```
        GET {BASE_URL}/api/v1/books/{isbn}
        ```
        
    - request
        
        ```
        GET {BASE_URL}/api/v1/books/1234567891011
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
          "isbn": 1234567891011,
          "additionSymbol": null,
          "name": "test book name",
          "authors": "test book authors",
          "publisher": "test book publisher",
          "publicationYear": 2024,
          "classNo": null,
          "className": null,
          "description": "test book description",
          "imageUrl": "test book image url",
          "approvalStatus": "APPROVE"
        }
        ```
        
    - 400 Bad Request
        
        ```
        {
        	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
        	"details": {
        		"isbn": "ISBN 번호는 13자리의 숫자만 입력 가능합니다."
        	}
        }
        ```
        
    - 403 Forbidden
        
        ```
        {
        	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
        	"detail":"아직 승인되지 않은 도서입니다."
        }
        ```
        
    - 404 Not Found
        
        ```
        {
        	"message":"요청하신 정보를 찾을 수 없습니다.",
        	"detail":"해당 도서는 북적북적 사이트에 없는 도서이거나 존재하지 않는 도서입니다."
        }
        ```
        

### Create Book API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        POST {BASE_URL}/api/v1/books
        ```
        
    - request
        
        ```
        {
            "isbn": 1234567891011,
            "imageUrl": "test image url",
            "name": "test name",
            "authors": "test authors",
            "publisher": "test publisher",
            "publicationYear": 2024,
            "classNo": "test class no",
            "className": "test class name",
            "description": "test description"
        }
        ```
        
- **Response**
    - 201 Created
        
        ```
        {
            "message": "도서가 성공적으로 요청되었습니다."
        }
        ```
        
    - 400 Bad Request
        
        ```
        {
            "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            "details": {
                "imageUrl": "널이어서는 안됩니다",
                "isbn": "ISBN 번호는 13자리의 숫자만 입력 가능합니다.",
                "classNo": "널이어서는 안됩니다",
                "name": "널이어서는 안됩니다",
                "publisher": "널이어서는 안됩니다",
                "description": "널이어서는 안됩니다",
                "publicationYear": "널이어서는 안됩니다",
                "className": "널이어서는 안됩니다",
                "authors": "널이어서는 안됩니다"
            }
        }
        ```
        
    - 401 Unauthorized
        
        ```
        {
            "message": "인증 정보가 유효하지 않습니다.",
            "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
        }
        ```
        
    - 409 Conflict
        
        ```
        {
            "message": "이미 존재하는 리소스입니다.",
            "detail": "이미 등록되어 있는 도서입니다. (WAIT/APPROVE/REJECT)"
        }
        ```
        

### Update Book Status API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        POST {BASE_URL}/api/v1/books
        ```
        
    - request
        
        ```
        {
            "isbn": 1234567891011,
            "imageUrl": "test image url",
            "name": "test name",
            "authors": "test authors",
            "publisher": "test publisher",
            "publicationYear": 2024,
            "classNo": "test class no",
            "className": "test class name",
            "description": "test description"
        }
        ```
        
- **Response**
    - 201 Created
        
        ```
        {
            "message": "도서가 성공적으로 요청되었습니다."
        }
        ```
        
    - 400 Bad Request
        
        ```
        {
            "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            "details": {
                "imageUrl": "널이어서는 안됩니다",
                "isbn": "ISBN 번호는 13자리의 숫자만 입력 가능합니다.",
                "classNo": "널이어서는 안됩니다",
                "name": "널이어서는 안됩니다",
                "publisher": "널이어서는 안됩니다",
                "description": "널이어서는 안됩니다",
                "publicationYear": "널이어서는 안됩니다",
                "className": "널이어서는 안됩니다",
                "authors": "널이어서는 안됩니다"
            }
        }
        ```
        
    - 401 Unauthorized
        
        ```
        {
            "message": "인증 정보가 유효하지 않습니다.",
            "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
        }
        ```
        
    - 409 Conflict
        
        ```
        {
            "message": "이미 존재하는 리소스입니다.",
            "detail": "이미 등록되어 있는 도서입니다. (WAIT/APPROVE/REJECT)"
        }
        ```
        

---

## BookTrade

### List BookTrade API

- **Request**
    - url
        
        ```
        headers: Bearer {ACCESS_TOKEN}
        GET {BASE_URL}/api/v1/books/list/{BOROUGH_ID}
        ```
        
    - request
        
        ```
        {BASE_URL}/api/v1/trades/list/5
        ```
        
- **Response**
    - 200 Ok
        
        ```
        [
          {
            "id": 1,
            "tradeType": "LEND",
            "tradeYn": "N",
            "deleteYn": "N",
            "rentalCost": 0,
            "createDate": "2024-06-18",
            "book": {
              "imageUrl": "http://image.aladin.co.kr/product/1950/55/cover/8960773417_1.jpg",
              "name": "토비의 스프링 3.1",
              "publicationYear": 2012,
              "authors": "이일민 지음"
            },
            "user": {
              "nickname": "구수하게"
            },
            "borough": {
              "name": "관악구"
            }
          },
          {
            "id": 2,
            "tradeType": "BORROW",
            "tradeYn": "N",
            "deleteYn": "N",
            "rentalCost": 0,
            "createDate": "2024-06-18",
            "book": {
              "imageUrl": "https://image.aladin.co.kr/product/29875/95/cover/8965403340_1.jpg",
              "name": "자바 앱 개발 워크북 :성장하는 개발자를 만드는 실무형 로드맵 ",
              "publicationYear": 2022,
              "authors": "구멍가게 코딩단 지음"
            },
            "user": {
              "nickname": "구수하게"
            },
            "borough": {
              "name": "관악구"
            }
          }
        ]
        ```
        
    - 400 Bad Request
        - 유효하지 않은 BOROUGH_ID
            
            ```
            {
            	"message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
            	"details": {
            		"boroughId": "지역 번호는 1부터 25까지의 숫자만 입력 가능합니다."
            	}
            }
            ```
            
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            	{
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 403 Forbidden
        - 사용자 정보의 자치구와 다른 자치구 거래목록 요청
            
            ```
            {
                "message": "잘못된 권한입니다.",
                "detail": "자신의 자치구 게시판/게시글만 조회할 수 있습니다."
            }
            ```
            

### Detail BookTrade API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        GET {BASE_URL}/api/v1/trades/{isbn}
        ```
        
    - request
        
        ```
        {BASE_URL}/api/v1/trades/1
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
            "id": 1,
            "rentalCost": 0,
            "content": "ㅋㅋ",
            "longitude": 129.35013823,
            "latitude": 36.01518104,
            "tradeType": "LEND",
            "tradeYn": "N",
            "deleteYn": "N",
            "limitedDate": "2024-06-27",
            "createDate": "2024-06-18",
            "user": {
                "nickname": "구수하게",
                "tradeWriterId": 4
            },
            "book": {
                "isbn": 9788960773417,
                "imageUrl": "http://image.aladin.co.kr/product/1950/55/cover/8960773417_1.jpg",
                "name": "토비의 스프링 3.1",
                "publisher": "에이콘출판",
                "publicationYear": 2012,
                "description": "스프링을 처음 접하거나 스프링을 경험했지만 스프링이 어렵게 느껴지는 개발자부터 스프링을 활용한 아키텍처를 설계하고 프레임워크를 개발하려고 하는 아키텍트에 이르기까지 모두 참고할 수 있는 스프링 완벽 바이블이다.",
                "className": "총류 > 총류 > 프로그래밍, 프로그램, 데이터",
                "authors": "이일민 지음"
            }
        }
        ```
        
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            {
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 403 Forbidden
        - 자치구가 다른 거래 글 조회
            
            ```
            {
                "message": "잘못된 권한입니다.",
                "detail": "자신의 자치구 게시판/게시글만 조회할 수 있습니다."
            }
            ```
            
    - 404 Not Found
        - 삭제된 게시글 조회
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "삭제된 게시물 입니다."
            }
            ```
            
        - 존재하지 않는 BookTrade id
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "거래 번호를 찾을 수 없습니다."
            }
            ```
            

### Create BookTrade API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        POST {BASE_URL}/api/v1/trades
        ```
        
    - request
        
        ```
        {
            "isbn": 9788960883802,
            "tradeType": "BORROW",
            "rentalCost": 10,
            "limitedDate": "2024-06-30",
            "content": "testContent",
            "latitude": "12.3",
            "longitude": 23.4
        }
        ```
        
- **Response**
    - 201 Created
        
        ```
        {
            "message": "거래글이 등록되었습니다."
        }
        ```
        
    - 400 Bad Request
        - 유효하지 않은 형식
            
            ```
            {
                "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
                "details": {
                    "rentalCost": "금액은 0부터 100000까지의 정수값만 입력 가능합니다.",
                    "isbn": "ISBN 번호는 13자리의 숫자만 입력 가능합니다.",
                    "limitedDate": "오늘 날짜 이후로 설정해주세요."
                }
            }
            ```
            
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            {
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 404 Not Found
        - isbn에 해당하는 도서가 없음
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "해당 도서는 북적북적 사이트에 없는 도서이거나 존재하지 않는 도서입니다."
            }
            ```
            

### Update BookTrade API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        PUT {BASE_URL}/api/v1/trades/{TRADE_ID}
        ```
        
    - request
        
        ```
        {BASE_URL}/api/v1/trades/1
        
        {
            "tradeType": "BORROW",
            "rentalCost": 10,
            "limitedDate": "2024-06-22",
            "content": "update testContent",
            "latitude": "12.3",
            "longitude": 23.4
        }
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
            "message": "수정을 성공했습니다."
        }
        ```
        
    - 400 Bad Request
        - 유효하지 않은 형식
            
            ```
            {
                "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
                "details": {
                    "rentalCost": "금액은 0부터 100000까지의 정수값만 입력 가능합니다.",
                    "tradeType": "trade type은 borrow, lend만 가능합니다.",
                    "limitedDate": "오늘 날짜 이후로 설정해주세요."
                }
            }
            ```
            
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            {
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 403 Forbidden
        - 자신의 게시글이 아님
            
            ```
            {
                "message": "잘못된 권한입니다.",
                "detail": "자신의 게시글만 수정/삭제할 수 있습니다."
            }
            ```
            
    - 404 Not Found
        - tradeId에 해당하는 게시글이 없음
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "거래 번호를 찾을 수 없습니다."
            }
            ```
            

### Update BookTrade Status API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        PATCH {BASE_URL}/api/v1/trades/{TRADE_ID}
        ```
        
    - request
        
        ```
        {BASE_URL}/api/v1/trades/1
        
        {
            "tradeYn": "N"
        }
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
            "message": "거래 상태가 변경되었습니다."
        }
        ```
        
    - 400 Bad Request
        - 유효하지 않은 형식
            
            ```
            {
                "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
                "details": {
                    "tradeYn": "status는 y, n만 가능합니다."
                }
            }
            ```
            
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            {
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 403 Forbidden
        - 자신의 게시글이 아님
            
            ```
            {
                "message": "잘못된 권한입니다.",
                "detail": "자신의 게시글만 수정/삭제할 수 있습니다."
            }
            ```
            
    - 404 Not Found
        - tradeId에 해당하는 게시글이 없음
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "거래 번호를 찾을 수 없습니다."
            }
            ```
            

### Delete BookTrade API

- **Request**
    - url
        
        ```
        header: Bearer {ACCESS_TOKEN}
        DELETE {BASE_URL}/api/v1/trades/{TRADE_ID}
        ```
        
    - request
        
        ```
        {BASE_URL}/api/v1/trades/1
        ```
        
- **Response**
    - 200 Ok
        
        ```
        {
            "message": "삭제를 성공했습니다."
        }
        ```
        
    - 400 Bad Request
        - 유효하지 않은 형식
            
            ```
            {
                "message": "요청이 유효하지 않습니다. 다시 한번 확인해주세요.",
                "details": {
                    "tradeYn": "status는 y, n만 가능합니다."
                }
            }
            ```
            
    - 401 Unauthorized
        - 인증되지 않은 요청
            
            ```
            {
                "message": "인증 정보가 유효하지 않습니다.",
                "detail": "회원 정보가 없습니다. 로그인 후 이용해주세요"
            }
            ```
            
    - 403 Forbidden
        - 자신의 게시글이 아님
            
            ```
            {
                "message": "잘못된 권한입니다.",
                "detail": "자신의 게시글만 수정/삭제할 수 있습니다."
            }
            ```
            
    - 404 Not Found
        - 이미 삭제된 게시글
            
            ```
            {
                "message": "요청하신 정보를 찾을 수 없습니다.",
                "detail": "삭제된 게시물 입니다."
            }
            ```
