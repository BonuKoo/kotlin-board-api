# java-jsp-board

Java + Spring Boot + JSP로 구현한 게시판입니다.
브라우저용 **JSP 화면**과 Jetpack Compose 안드로이드 앱([compose-board-app](#연동-프로젝트))이 쓰는
**REST API**를 한 서버에서 함께 제공합니다.

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F?logo=springboot&logoColor=white)
![JSP](https://img.shields.io/badge/JSP%20%2F%20JSTL-3.0-E76F00)
![Tomcat](https://img.shields.io/badge/Tomcat-11.0-F8DC75?logo=apachetomcat&logoColor=black)
![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?logo=gradle&logoColor=white)

---

## 프로젝트 소개

게시글의 생성·조회·수정·삭제(CRUD)를 제공합니다.
Controller / Service / Repository 계층을 분리하고, 엔티티와 DTO를 구분하여
영속성 모델이 화면이나 API 응답에 직접 노출되지 않도록 설계했습니다.

**주요 관심사**

- 계층형 아키텍처와 관심사 분리
- Entity ↔ DTO 변환 책임의 명확한 배치 (Service 계층)
- 하나의 서비스 계층 위에 **서버 렌더링(JSP)** 과 **JSON API** 두 진입점을 얹기
- 실제 모바일 클라이언트와의 HTTP/JSON 연동

---

## 연동 프로젝트

| 리포지토리 | 역할 | 기술 |
|---|---|---|
| **java-jsp-board** (현재) | 웹 화면 + REST API 서버 | Java, Spring Boot, JSP, JPA |
| [compose-board-app](https://github.com/BonuKoo/compose-board-app) | 안드로이드 클라이언트 | Kotlin, Jetpack Compose, Retrofit |

> 두 리포지토리는 별도로 관리되며 **HTTP/JSON 계약으로만 연결**됩니다.
> JSP 화면을 추가하면서도 `/board` 엔드포인트는 그대로 두어 앱이 영향을 받지 않습니다.

---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 4.0.7, Spring Web MVC |
| 뷰 | JSP, JSTL 3.0 (Jakarta), EL |
| 서블릿 컨테이너 | 내장 Tomcat 11 (Jasper) |
| 영속성 | Spring Data JPA, Hibernate |
| 데이터베이스 | H2 (In-Memory) |
| API 문서 | springdoc-openapi 2.2.0 (Swagger UI) |
| 직렬화 | Jackson |
| 빌드 | Gradle 9.5.1, **war 패키징** |
| 테스트 | JUnit 5, AssertJ, MockMvc |

---

## 구조

| 계층 | 책임 |
|---|---|
| `BoardViewController` | JSP 화면 라우팅, 폼 처리 (`/boards`) |
| `BoardApiController` | JSON API 매핑 (`/board`) |
| `HomeController` | 루트(`/`)를 목록으로 리다이렉트 |
| `BoardService` | 비즈니스 로직, Entity ↔ DTO 변환 |
| `BoardRepository` | 영속성 접근 (`JpaRepository` 상속) |
| `BoardEntity` | 도메인 모델, 상태 변경 메서드 |
| `BoardDto` | 계층 간·클라이언트 간 데이터 전달 |

---

## 화면 (JSP)

Base URL: `http://localhost:8080`

| Method | Endpoint | 설명 | 결과 |
|---|---|---|---|
| `GET` | `/` | 루트 | `/boards` 로 리다이렉트 |
| `GET` | `/boards` | 목록 | `board/list.jsp` |
| `GET` | `/boards/new` | 작성 폼 | `board/form.jsp` |
| `POST` | `/boards` | 작성 처리 | `/boards` 로 리다이렉트 |
| `GET` | `/boards/{id}` | 상세 | `board/detail.jsp` / 404 화면 |
| `GET` | `/boards/{id}/edit` | 수정 폼 | `board/form.jsp` |
| `POST` | `/boards/{id}/edit` | 수정 처리 | `/boards/{id}` 로 리다이렉트 |
| `POST` | `/boards/{id}/delete` | 삭제 처리 | `/boards` 로 리다이렉트 |

HTML `form`은 GET과 POST만 보낼 수 있어 수정·삭제도 POST로 받습니다.
쓰기 요청 뒤에는 리다이렉트로 응답해 새로고침 시 중복 전송을 막습니다(PRG 패턴).

화면은 JSTL과 EL로 그립니다. `c:forEach`로 목록을 돌고, `c:choose`로 등록/수정 폼을 가르며,
사용자 입력은 `c:out`으로 이스케이프해 출력합니다. 머리말·꼬리말은 `.jspf` 조각으로 분리해
정적 include 합니다.

---

## API 명세

앱과의 계약이므로 경로·상태 코드·필드 이름을 바꾸지 않습니다.

| Method | Endpoint | 설명 | Request Body | Response |
|---|---|---|--|---|
| `POST` | `/board` | 게시글 생성 | `BoardDto` | `200 OK` |
| `GET` | `/board` | 전체 조회 |  | `200 OK` · `BoardDto[]` |
| `GET` | `/board/{id}` | 단건 조회 |  | `200 OK` · `BoardDto` / `404` |
| `PATCH` | `/board` | 게시글 수정 | `BoardDto` (`id` 필수) | `200 OK` / `404` |
| `DELETE` | `/board/{id}` | 게시글 삭제 |  | `200 OK` / `404` |

### 오류 응답

존재하지 않는 게시글을 조회·수정·삭제하면 `404 Not Found` 를 반환합니다.
예외 처리는 `@ControllerAdvice` 에 모여 있어 컨트롤러는 정상 흐름만 다룹니다.

같은 예외를 진입점에 따라 다르게 옮깁니다.
API는 JSON을, 브라우저는 사람이 읽는 HTML 화면을 받아야 하기 때문입니다.

| 진입점 | 처리 | 결과 |
|---|---|---|
| `/board/**` | `ApiExceptionHandler` | `404` + JSON |
| `/boards/**` | `ViewExceptionHandler` | `404` + `error/notFound.jsp` |

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "게시글을 찾을 수 없습니다. id=999",
  "timestamp": "2026-08-07T09:05:34.476745"
}
```
---

| 대상 | 개수 | 확인하는 것 |
|---|---|---|
| `BoardServiceTest` | 10 | 생성·조회·수정·삭제, 없는 게시글에 대한 예외, 수정 시 작성자 유지 |
| `BoardApiTest` | 9 | 상태 코드와 JSON 필드, 404 응답, 한글 왕복 |
| `BoardViewTest` | 9 | 뷰 이름과 forward 경로, 모델 속성, 폼 처리 후 리다이렉트, 404 화면 |
| `BoardApplicationTests` | 1 | 애플리케이션 컨텍스트 로딩 |

저장소를 흉내 내지 않고 실제 H2 로 검증합니다.
`BoardServiceTest` 는 `@DataJpaTest` 로 JPA 계층만 올려 엔티티 매핑까지 함께 확인하고,
`BoardApiTest` 와 `BoardViewTest` 는 `MockMvc` 로 실제 요청을 보냅니다.

앱과는 HTTP 계약으로만 연결되므로, 상태 코드나 JSON 필드 이름이 바뀌면
앱이 조용히 깨집니다. `BoardApiTest` 가 그 경계를 지킵니다.

> `MockMvc` 에는 서블릿 컨테이너가 없어 `.jsp` 가 실제로 렌더링되지는 않습니다.
> `BoardViewTest` 는 어떤 뷰로 forward 되는지까지 확인하고,
> 화면이 실제로 그려지는지는 앱을 띄워 확인합니다.

---

### 개발용 도구

| 도구 | 주소 |
|---|---|
| 게시판 화면 | http://localhost:8080/boards |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI 스펙 | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |


---
