# 🛒 Shoppingmall Goods Order API

> Java JDBC 기반으로 개발한 쇼핑몰 프로젝트를 **Spring Boot + JPA + Spring Security**로 전환한 RESTful API 서버

<br>

## 🔗 배포 링크

| 항목 | 링크 |
|------|------|
| **Swagger UI** | http://54.180.26.225:8080/swagger-ui/index.html |
| **API Base URL** | http://54.180.26.225:8080 |

<br>

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 4.0 |
| ORM | Spring Data JPA, Hibernate 7 |
| Security | Spring Security 7, JWT (jjwt 0.11.5) |
| Database | MySQL 8.0 |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Server | AWS EC2 (Ubuntu) |

<br>

## ⚙️ 시스템 아키텍처

```
┌─────────────────────────────────────────────────────┐
│                   GitHub Repository                  │
│                                                      │
│  push → main branch                                  │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                  GitHub Actions CI/CD                │
│                                                      │
│  1. JDK 21 Setup                                    │
│  2. mvn clean package -DskipTests                   │
│  3. SSH → jar + Dockerfile to EC2                   │
│  4. SSH → docker build & docker compose up          │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                 AWS EC2 (Ubuntu)                     │
│                                                      │
│  ┌─────────────────┐    ┌────────────────────────┐  │
│  │   app container  │    │     db container       │  │
│  │  Spring Boot    │◄──►│     MySQL 8.0          │  │
│  │  :8080          │    │     :3306              │  │
│  └─────────────────┘    └────────────────────────┘  │
│         Docker Compose (healthcheck 연동)             │
└─────────────────────────────────────────────────────┘
```

<br>

## 📁 프로젝트 구조

```
src/main/java/com/shoppingmall/goods/
├── config/
│   ├── JwtFilter.java          # JWT 인증 필터
│   ├── JwtUtil.java            # JWT 토큰 생성/검증
│   ├── SecurityConfig.java     # Spring Security 설정
│   └── SwaggerConfig.java      # Swagger 설정
├── controller/
│   ├── AuthController.java     # 회원가입, 로그인
│   ├── CustomerController.java # 회원 관리
│   ├── GoodsController.java    # 상품 관리
│   ├── OrderController.java    # 주문 관리
│   └── CartController.java     # 장바구니 관리
├── Service/
│   ├── CustomerService.java
│   ├── GoodsService.java
│   ├── OrderService.java       # 주문 생성 핵심 로직
│   └── CartService.java
├── Repository/
│   ├── CustomerRepository.java
│   ├── GoodsRepository.java
│   ├── OrdersRepository.java
│   ├── OrderLineRepository.java
│   └── CartRepository.java
├── entity/
│   ├── Customer.java
│   ├── Goods.java
│   ├── Orders.java
│   ├── OrderLine.java
│   └── Cart.java
└── dto/
    ├── RegisterRequestDto.java
    ├── LoginRequestDto.java
    ├── CustomerResponseDto.java
    ├── OrderCreateRequestDto.java
    └── OrderItemDto.java
```

<br>

## 🗄 ERD

```
┌──────────────┐        ┌──────────────────┐        ┌───────────────┐
│   Customer   │        │     Orders       │        │   OrderLine   │
├──────────────┤        ├──────────────────┤        ├───────────────┤
│ userId (PK)  │──┐     │ orderId (PK, AI) │──┐     │ orderLineID   │
│ userPwd      │  └────►│ userId (FK)      │  └────►│ (PK, AI)      │
│ userName     │        │ orderDate        │        │ orderId (FK)  │
│ regDate      │        │ address          │        │ goodsId (FK)  │
│ role         │        │ totalAmount      │        │ unitPrice     │
└──────────────┘        └──────────────────┘        │ qty           │
                                                     │ amount        │
┌──────────────┐        ┌──────────────────┐        └───────────────┘
│    Goods     │        │      Cart        │
├──────────────┤        ├──────────────────┤
│ goodsId (PK) │◄──────►│ cartId (PK, AI) │
│ goodsName    │        │ userId           │
│ goodsPrice   │        │ goodsId (FK)     │
│ stock        │        │ qty              │
│ regdate      │        └──────────────────┘
└──────────────┘
```

<br>

## 📌 API 명세

### 🔓 인증 (Auth) — 인증 불필요

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/auth/register` | 회원가입 |
| POST | `/auth/login` | 로그인 (JWT 토큰 발급) |

<details>
<summary>Request / Response 예시</summary>

**POST /auth/register**
```json
// Request
{
  "userId": "user1",
  "userPwd": "user1234",
  "userName": "홍길동"
}

// Response
"회원가입 완료"
```

**POST /auth/login**
```json
// Request
{
  "userId": "user1",
  "userPwd": "user1234"
}

// Response
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6..."
```
</details>

---

### 🔒 회원 (Customer) — JWT 필요

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/customer` | 전체 회원 조회 |
| GET | `/customer/{userId}` | 특정 회원 조회 |
| POST | `/customer` | 회원 등록 |
| DELETE | `/customer/{userId}` | 회원 삭제 |

---

### 🔒 상품 (Goods) — JWT 필요

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/goods` | 전체 상품 조회 |
| GET | `/goods/{goodsId}` | 특정 상품 조회 |
| POST | `/goods` | 상품 등록 |
| PUT | `/goods/{goodsId}` | 상품 수정 |
| DELETE | `/goods/{goodsId}` | 상품 삭제 |

<details>
<summary>Request / Response 예시</summary>

**POST /goods**
```json
{
  "goodsId": "G001",
  "goodsName": "나이키 운동화",
  "goodsPrice": 120000,
  "stock": 50,
  "regdate": "2026-06-08T00:00:00"
}
```
</details>

---

### 🔒 주문 (Order) — JWT 필요

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/order` | 전체 주문 조회 |
| GET | `/order/{orderId}` | 특정 주문 조회 |
| GET | `/order/user/{userId}` | 특정 회원 주문 조회 |
| POST | `/order` | 주문 생성 |

<details>
<summary>Request / Response 예시</summary>

**POST /order**
```json
// Request
{
  "address": "서울시 강남구 테헤란로 123",
  "items": [
    { "goodsId": "G001", "qty": 2 },
    { "goodsId": "G002", "qty": 1 }
  ]
}

// Response
{
  "orderId": 1,
  "orderDate": "2026-06-08T08:30:00",
  "address": "서울시 강남구 테헤란로 123",
  "totalAmount": 240000,
  "userId": "user1"
}
```
</details>

---

### 🔒 장바구니 (Cart) — JWT 필요

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/cart/{userId}` | 특정 회원 장바구니 조회 |
| POST | `/cart` | 장바구니 추가 |
| DELETE | `/cart/{cartId}` | 장바구니 항목 삭제 |

<details>
<summary>Request / Response 예시</summary>

**POST /cart**
```json
{
  "userId": "user1",
  "goodsId": "G001",
  "qty": 2
}
```
</details>

<br>

## 🔐 인증 방식

JWT(JSON Web Token) 기반 Stateless 인증을 사용합니다.

```
1. POST /auth/login 으로 로그인
2. 응답으로 JWT 토큰 수신
3. 이후 모든 요청 Header에 포함:
   Authorization: Bearer {token}
```

> Swagger에서 테스트 시 우측 상단 **Authorize 🔒** 버튼 클릭 후 `Bearer {토큰}` 입력

<br>

## 💡 핵심 구현 내용

### 주문 생성 트랜잭션 처리

주문 생성 시 아래 작업이 하나의 트랜잭션으로 처리됩니다.

```
POST /order 요청
  ├── 1. 상품 존재 여부 확인
  ├── 2. 재고 수량 확인 (부족 시 예외 발생 → 전체 롤백)
  ├── 3. 총 결제금액 자동 계산 (단가 × 수량 합산)
  ├── 4. Orders 저장 (JWT에서 userId 자동 추출)
  ├── 5. OrderLine 저장 (상품별 주문 상세)
  └── 6. Goods 재고 차감
```

### Spring Security + JWT 인증 흐름

```
HTTP 요청
  │
  ▼
JwtFilter (OncePerRequestFilter)
  ├── Authorization 헤더 없음 → 익명 사용자로 통과
  └── Bearer 토큰 있음
        ├── 토큰 검증 실패 → 익명 사용자로 통과
        └── 토큰 검증 성공 → SecurityContext에 인증 정보 등록
  │
  ▼
AuthorizationFilter
  ├── /auth/**, /swagger-ui/**, /v3/api-docs/** → 허용
  └── 그 외 → 인증 여부 확인
```

<br>

## 🚀 로컬 실행 방법

### 사전 요구사항
- Java 21
- Docker & Docker Compose

### 실행

```bash
# 1. 레포지토리 클론
git clone https://github.com/minseo9451/shoppingmall-goods-order.git
cd shoppingmall-goods-order

# 2. 환경변수 설정
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/shoppingmall_db
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_secret_key_minimum_32_characters_long

# 3. Docker Compose 실행
docker compose up -d
```

```
http://localhost:8080/swagger-ui/index.html
```

<br>

## 🔄 CI/CD 파이프라인

`main` 브랜치에 push 시 자동으로 EC2에 배포됩니다.

```
push to main
    │
    ▼
GitHub Actions
    ├── 1. JDK 21 설치
    ├── 2. Maven 빌드
    ├── 3. EC2에 jar + Dockerfile 전송 (SSH/SCP)
    └── 4. EC2에서 컨테이너 재배포
            ├── docker compose down
            ├── docker build -t app .
            └── docker compose up -d
```

**GitHub Secrets 설정 목록**

| Secret | 설명 |
|--------|------|
| `EC2_HOST` | EC2 퍼블릭 IP |
| `EC2_KEY` | EC2 SSH 프라이빗 키 |
| `DB_USERNAME` | MySQL 사용자명 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `DB_ROOT_PASSWORD` | MySQL root 비밀번호 |
| `DB_URL_DOCKER` | Docker 내부 DB 접속 URL |
| `JWT_SECRET` | JWT 서명 키 |

<br>

## 👤 개발자

| 이름 | GitHub |
|------|--------|
| 신민서 | [@minseo9451](https://github.com/minseo9451) |
