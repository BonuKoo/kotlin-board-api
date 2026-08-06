# kotlin-board-api

Kotlin + Spring Boot로 구현한 게시판 REST API 서버입니다.
Jetpack Compose 안드로이드 앱([compose-board-app](#연동-프로젝트))의 백엔드로 동작합니다.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/JDK-17-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?logo=gradle&logoColor=white)

---

## 프로젝트 소개

게시글의 생성·조회·수정·삭제(CRUD)를 제공하는 REST API입니다.
Controller / Service / Repository 계층을 분리하고, 엔티티와 DTO를 구분하여
영속성 모델이 API 응답에 직접 노출되지 않도록 설계했습니다.

**주요 관심사**

- 계층형 아키텍처와 관심사 분리
- Entity ↔ DTO 변환 책임의 명확한 배치 (Service 계층)
- 실제 모바일 클라이언트와의 HTTP/JSON 연동

---

## 연동 프로젝트

| 리포지토리 | 역할 | 기술 |
|---|---|---|
| **kotlin-board-api** (현재) | REST API 서버 | Kotlin, Spring Boot, JPA |
| [compose-board-app](https://github.com/BonuKoo/compose-board-app) | 안드로이드 클라이언트 | Kotlin, Jetpack Compose, Retrofit |

> 두 리포지토리는 별도로 관리되며 **HTTP/JSON 계약으로만 연결**됩니다.
---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Kotlin 2.2.21 (JDK 17) |
| 프레임워크 | Spring Boot 4.0.7, Spring Web MVC |
| 영속성 | Spring Data JPA, Hibernate |
| 데이터베이스 | H2 (In-Memory) |
| API 문서 | springdoc-openapi 2.2.0 (Swagger UI) |
| 직렬화 | Jackson (jackson-module-kotlin) |
| 빌드 | Gradle 9.5.1 |
| 테스트 | JUnit 5, kotlin-test |

---

| 계층 | 책임 |
|---|---|
| `BoardController` | HTTP 요청/응답 매핑, 라우팅 |
| `BoardService` | 비즈니스 로직, Entity ↔ DTO 변환 |
| `BoardRepository` | 영속성 접근 (`JpaRepository` 상속) |
| `BoardEntity` | 도메인 모델, 상태 변경 메서드 |
| `BoardDto` | 계층 간·클라이언트 간 데이터 전달 |

---

## API 명세

Base URL: `http://localhost:8080`

| Method | Endpoint | 설명 | Request Body | Response |
|---|---|---|--|---|
| `POST` | `/board` | 게시글 생성 | `BoardDto` | `200 OK` |
| `GET` | `/board` | 전체 조회 |  | `200 OK` · `BoardDto[]` |
| `GET` | `/board/{id}` | 단건 조회 |  | `200 OK` · `BoardDto` |
| `PATCH` | `/board` | 게시글 수정 | `BoardDto` (`id` 필수) | `200 OK` |
| `DELETE` | `/board/{id}` | 게시글 삭제 |  | `200 OK` |

---

### 개발용 도구

| 도구 | 주소 |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI 스펙 | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |


> **안드로이드 앱에서 연결할 때**
> 에뮬레이터·실기기는 `localhost`를 인식하지 못합니다.
> 서버가 실행 중인 PC의 **LAN IP**(예: `http://???.???.?.?:8080`)를 사용하고,
> 방화벽에서 8080 포트 인바운드를 허용해야 합니다.


---