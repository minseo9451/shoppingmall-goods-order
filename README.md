# Shoppingmall Goods Order API

Java JDBC로 구현한 쇼핑몰 프로젝트를 Spring Boot + JPA + Spring Security로 전환한 RESTful API 서버입니다.

**Swagger UI** → http://54.180.26.225:8080/swagger-ui/index.html

<br>

## 프로젝트 배경

JDBC + 순수 SQL로 작성한 기존 코드를 Spring 생태계로 전환하는 것이 목표였습니다. 단순 포팅이 아니라, 전환 과정에서 만나는 **Spring Security 설정 충돌, Docker 컨테이너 기동 순서 문제, CI/CD 파이프라인 트러블슈팅** 등 실제 운영 환경에서 생기는 문제들을 직접 해결하는 데 집중했습니다.

<br>

## 기술 스택

Spring Boot 4.0 · Spring Security 7 · Spring Data JPA · MySQL 8.0 · JWT · Docker · GitHub Actions · AWS EC2

<br>

## 아키텍처

```
GitHub push → GitHub Actions (Maven 빌드 → SSH/SCP → EC2)
                                                        │
                                              ┌─────────▼─────────┐
                                              │   Docker Compose  │
                                              │                   │
                                              │  ┌─────────────┐  │
                                              │  │  Spring App │  │
                                              │  │   :8080     │  │
                                              │  └──────┬──────┘  │
                                              │         │ depends_on (healthcheck)
                                              │  ┌──────▼──────┐  │
                                              │  │  MySQL 8.0  │  │
                                              │  │   :3306     │  │
                                              │  └─────────────┘  │
                                              └───────────────────┘
```

<br>

## ERD

```
Customer ──┐
           ├──► Orders ──► OrderLine ◄── Goods
           │
           └──► Cart ◄──────────────── Goods
```

| 테이블 | 주요 컬럼 |
|--------|-----------|
| Customer | userId(PK), userPwd, userName, role |
| Orders | orderId(PK, AI), userId(FK), address, totalAmount |
| OrderLine | orderLineId(PK, AI), orderId(FK), goodsId(FK), unitPrice, qty, amount |
| Goods | goodsId(PK), goodsName, goodsPrice, stock |
| Cart | cartId(PK, AI), userId, goodsId(FK), qty |

<br>

## 핵심 구현

### 트랜잭션 기반 주문 생성

주문 한 건에 여러 상품이 포함될 수 있고, 중간에 재고가 부족하면 전체를 롤백해야 합니다.

```
POST /order
  ├── 1. 전체 상품 재고 선검증 (부족 시 즉시 예외 → 롤백)
  ├── 2. totalAmount = Σ(단가 × 수량) 계산
  ├── 3. Orders 저장 (userId는 JWT SecurityContext에서 추출)
  ├── 4. OrderLine 저장 (상품별 주문 상세)
  └── 5. 재고 차감
```

재고 검증을 저장 전에 몰아서 처리함으로써, 일부만 저장된 채 롤백되는 부분 실패 상황을 방지했습니다.

---

### Spring Security 7 + JWT 충돌 해결

Spring Boot 4.x에서 `UserDetailsService` 빈을 등록하지 않으면 `InMemoryUserDetailsManager`가 자동 설정되어, JWT 필터와 충돌합니다. JWT 기반 Stateless 인증에서는 `UserDetailsService`가 실제로 필요 없으므로, 예외를 던지는 빈을 명시적으로 등록해 자동 설정을 비활성화했습니다.

```java
@Bean
public UserDetailsService userDetailsService() {
    return username -> { throw new UsernameNotFoundException(username); };
}
```

---

### Docker Compose healthcheck로 기동 순서 보장

앱 컨테이너가 MySQL보다 먼저 기동되면 `Connection refused`로 즉시 종료됩니다. `healthcheck`와 `depends_on: condition: service_healthy` 조합으로 MySQL이 완전히 준비된 후에만 앱이 시작됩니다.

```yaml
db:
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval: 10s
    retries: 5

app:
  depends_on:
    db:
      condition: service_healthy
```

---

### Bean Validation으로 입력값 검증

컨트롤러에서 `@Valid`를 선언하면 DTO의 제약 조건을 자동으로 검증합니다.

```java
// RegisterRequestDto
@NotBlank(message = "아이디를 입력해주세요.")
@Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
private String userId;

@NotBlank(message = "비밀번호를 입력해주세요.")
@Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
private String userPwd;
```

Service 계층이 아닌 DTO에 제약 조건을 선언함으로써, 유효하지 않은 요청이 비즈니스 로직에 도달하기 전에 차단됩니다.

---

### 회원가입 시 아이디 중복 확인

저장 전에 `existsById()`로 중복 여부를 먼저 확인합니다. JPA의 `exists` 쿼리는 `SELECT 1`로 동작해 불필요한 엔티티 조회가 없습니다.

```java
if (customerService.existsById(dto.getUserId())) {
    throw new DuplicateException("이미 사용 중인 아이디입니다: " + dto.getUserId());
}
```

---

### 장바구니 중복 상품 수량 합산

같은 상품을 다시 담을 때 새 row를 생성하지 않고 기존 항목의 수량을 누적합니다.

```java
Optional<Cart> existing = cartRepository.findByUserIdAndGoodsId(cart.getUserId(), cart.getGoodsId());
if (existing.isPresent()) {
    Cart found = existing.get();
    found.setQty(found.getQty() + cart.getQty());
    return cartRepository.save(found);
}
return cartRepository.save(cart);
```

---

### ROLE 기반 접근 제어

`role` 필드를 JWT claim에 포함하고, `JwtFilter`에서 `SimpleGrantedAuthority("ROLE_USER/ADMIN")`으로 변환합니다. 관리자 전용 엔드포인트는 `@PreAuthorize("hasRole('ADMIN')")`으로 보호합니다.

```java
// JwtFilter
String role = jwtUtil.extractRole(token);
List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

// GoodsController
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ResponseEntity<ApiResponse<Goods>> create(@RequestBody Goods goods) { ... }
```

| 엔드포인트 | 접근 가능 |
|------------|----------|
| GET `/goods`, `/order`, `/cart` | 인증된 모든 사용자 |
| POST/PUT/DELETE `/goods` | ADMIN |
| GET/DELETE `/customer` | ADMIN |

---

### 비관적 락으로 재고 동시성 제어

동시 주문 요청이 들어오면 재고를 읽는 순간 `SELECT FOR UPDATE`로 행을 잠가, Race Condition으로 인한 재고 음수를 방지합니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT g FROM Goods g WHERE g.goodsId = :goodsId")
Optional<Goods> findByIdForUpdate(@Param("goodsId") String goodsId);
```

트랜잭션 A가 해당 상품 행을 잠그면, 트랜잭션 B는 A가 커밋/롤백될 때까지 대기합니다.

---

### 전역 예외 처리와 커스텀 예외

`@RestControllerAdvice`로 예외를 중앙에서 처리해 일관된 에러 응답을 반환합니다.

```
NotFoundException       → 404  (리소스 없음)
DuplicateException      → 409  (아이디 중복)
InsufficientStockException → 409  (재고 부족)
AuthException           → 401  (인증 실패)
MethodArgumentNotValidException → 400  (@Valid 검증 실패)
```

JWT 토큰 상태에 따른 응답도 명확히 구분합니다.

```
만료된 토큰  → 401 "토큰이 만료되었습니다."
위조된 토큰  → 401 "유효하지 않은 토큰입니다."
토큰 없음    → 401 "인증이 필요합니다."
권한 부족    → 403 "접근 권한이 없습니다."
```

---

### 통일된 API 응답 포맷

모든 성공 응답을 `ApiResponse<T>`로 감싸고 HTTP 상태코드를 정규화했습니다.

```json
{ "status": 200, "message": "ok", "data": { ... } }
{ "status": 201, "message": "created", "data": { ... } }
```

| 메서드 | 상태코드 |
|--------|---------|
| GET | 200 |
| POST | 201 Created |
| PUT | 200 |
| DELETE | 204 No Content |

---

### 서비스 레이어 트랜잭션 정비

모든 서비스 클래스에 `@Transactional(readOnly = true)`를 클래스 레벨에 선언하고, 쓰기 메서드만 `@Transactional`로 개별 오버라이드했습니다. `readOnly = true`는 Hibernate의 dirty checking을 생략해 조회 성능을 높입니다.

```java
@Transactional(readOnly = true)          // 클래스 기본값 — 조회 메서드 전체 적용
public class OrderService {

    @Transactional                        // 쓰기 메서드만 오버라이드
    public Orders createOrder(...) { ... }
}
```

<br>

## CI/CD

`main` 브랜치 push 시 GitHub Actions가 EC2에 자동 배포합니다.

```
push → JDK 21 + Maven 빌드 → SCP로 jar + Dockerfile 전송 → EC2에서 docker build + compose up
```

**데이터 영속성**: docker-compose.yml에 named volume(`mysql_data`)을 설정해, 배포 시 컨테이너가 재시작되어도 DB 데이터가 유지됩니다.

**환경변수 관리**: DB 접속 정보, JWT 시크릿 등 민감 정보는 모두 GitHub Secrets로 관리하며, 배포 시 docker-compose.yml에 환경변수로 주입됩니다.

<br>

## API

전체 명세는 Swagger UI에서 확인하세요. JWT가 필요한 엔드포인트는 `/auth/login`으로 토큰 발급 후 Authorize에 `Bearer {token}`을 입력하면 됩니다.

| 도메인 | 주요 엔드포인트 |
|--------|----------------|
| Auth | POST `/auth/register`, POST `/auth/login` |
| Customer | GET/POST `/customer`, DELETE `/customer/{userId}` |
| Goods | GET/POST/PUT/DELETE `/goods` |
| Order | GET `/order`, POST `/order`, GET `/order/user/{userId}` |
| Cart | GET `/cart/{userId}`, POST `/cart`, DELETE `/cart/{cartId}` |
