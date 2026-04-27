USE techqbank;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE review_logs;
TRUNCATE TABLE bookmarks;
TRUNCATE TABLE answer_histories;
TRUNCATE TABLE user_question_reviews;
TRUNCATE TABLE user_question_answers;
TRUNCATE TABLE question_tags;
TRUNCATE TABLE questions;
TRUNCATE TABLE tags;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, email, password, nickname, avatar_id, provider, provider_id, created_at, updated_at) VALUES
(1, 'junior@example.com', '$2a$10$dummyEncodedPasswordForJuniorUser0001', '주니어개발자', 0, 'LOCAL', 'local-junior-001', '2026-04-27 10:00:00', '2026-04-27 10:00:00'),
(2, 'mentor@example.com', '$2a$10$dummyEncodedPasswordForMentorUser0002', '면접멘토', 1, 'LOCAL', 'local-mentor-001', '2026-04-27 10:03:00', '2026-04-27 10:03:00'),
(3, 'private@example.com', '$2a$10$dummyEncodedPasswordForPrivateUser0003', '비공개연습러', 2, 'LOCAL', 'local-private-001', '2026-04-27 10:06:00', '2026-04-27 10:06:00'),
(4, NULL, '$2a$10$oauthDummyPasswordNotUsed0004', '깃허브로그인유저', 3, 'GITHUB', 'github-10001', '2026-04-27 10:09:00', '2026-04-27 10:09:00');

INSERT INTO tags (id, name) VALUES
(1, 'Java'),
(2, 'OOP'),
(3, 'Spring'),
(4, 'Spring Boot'),
(5, 'JPA'),
(6, 'Database'),
(7, 'SQL'),
(8, 'Transaction'),
(9, 'Network'),
(10, 'HTTP'),
(11, 'REST API'),
(12, 'Operating System'),
(13, 'Data Structure'),
(14, 'Algorithm'),
(15, 'Git'),
(16, 'Security'),
(17, 'Web'),
(18, 'Testing'),
(19, 'Docker'),
(20, 'Redis'),
(21, 'Clean Architecture'),
(22, 'Concurrency'),
(23, 'JVM'),
(24, 'Logging');

INSERT INTO questions (id, author_id, title, description, career_level, difficulty, visibility, my_notes, key_points, memo, created_at, updated_at) VALUES
(1, 2, '객체지향 프로그래밍이 무엇인지 설명해주세요.', '캡슐화, 상속, 추상화, 다형성을 단순 암기가 아니라 코드 설계 관점에서 설명하는 질문입니다.', 'JUNIOR', 'EASY', 'PUBLIC', '객체지향은 객체 간 협력과 역할 분리 관점에서 답변하면 좋습니다.', '캡슐화
추상화
상속
다형성
객체 간 협력', '기본 CS 질문', '2026-04-27 10:20:00', '2026-04-27 10:50:00'),
(2, 2, '클래스와 인터페이스의 차이는 무엇인가요?', '구현 상속과 역할 추상화의 차이, 다중 구현 가능성, 의존성 역전 관점까지 답변해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '인터페이스는 구현체 교체 가능성과 테스트 용이성까지 연결하면 좋습니다.', '클래스는 상태와 구현을 가질 수 있음
인터페이스는 역할과 계약을 표현
다형성
의존성 역전', 'Java/OOP 기본', '2026-04-27 10:22:00', '2026-04-27 10:52:00'),
(3, 2, '오버로딩과 오버라이딩의 차이를 설명해주세요.', '컴파일 타임 다형성과 런타임 다형성의 차이를 함께 설명하면 좋습니다.', 'JUNIOR', 'EASY', 'PUBLIC', '오버로딩은 같은 이름 다른 파라미터, 오버라이딩은 부모 메서드 재정의입니다.', '오버로딩
오버라이딩
메서드 시그니처
상속
다형성', '자주 나오는 쉬운 질문', '2026-04-27 10:24:00', '2026-04-27 10:54:00'),
(4, 2, 'equals()와 hashCode()를 함께 재정의해야 하는 이유는 무엇인가요?', 'HashMap, HashSet에서 객체 동등성을 판단하는 방식과 연결해서 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', 'equals로 같다면 hashCode도 같아야 한다는 규약이 핵심입니다.', '동등성 규약
HashMap
HashSet
해시 충돌
equals와 hashCode 일관성', '컬렉션과 연결', '2026-04-27 10:26:00', '2026-04-27 10:56:00'),
(5, 2, 'String, StringBuilder, StringBuffer의 차이를 설명해주세요.', '불변 객체, 문자열 연산 비용, 동기화 여부를 중심으로 정리해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '반복적인 문자열 조합에는 StringBuilder가 일반적으로 적합합니다.', 'String 불변
StringBuilder 가변
StringBuffer 동기화
문자열 연산 성능', 'Java 기본', '2026-04-27 10:28:00', '2026-04-27 10:58:00'),
(6, 2, '자바 컬렉션 프레임워크에서 List, Set, Map의 차이는 무엇인가요?', '중복 허용, 순서 보장, key-value 구조를 실제 사용 예시와 함께 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '자료구조의 특성을 실제 선택 기준과 연결해서 답변합니다.', 'List는 순서와 중복 허용
Set은 중복 제거
Map은 key-value
사용 사례', '자료구조 기본', '2026-04-27 10:30:00', '2026-04-27 11:00:00'),
(7, 2, 'HashMap은 내부적으로 어떻게 동작하나요?', '해시 함수, 버킷, 충돌, equals/hashCode와의 관계를 중심으로 답변해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '해시 충돌 처리와 Java 8 이후 트리화 개념까지 알면 좋습니다.', 'hashCode
bucket
collision
equals
red-black tree', '1~3년차 질문', '2026-04-27 10:32:00', '2026-04-27 11:02:00'),
(8, 2, 'JVM의 메모리 구조를 설명해주세요.', 'Heap, Stack, Method Area, PC Register, Native Method Stack을 간단히 구분해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', 'Heap과 Stack의 차이를 객체 생성, 메서드 호출 관점에서 설명합니다.', 'Heap
Stack
Method Area
PC Register
Native Method Stack', 'JVM 기본', '2026-04-27 10:34:00', '2026-04-27 11:04:00'),
(9, 2, '가비지 컬렉션이 필요한 이유와 동작 개념을 설명해주세요.', '객체 생명주기, reachability, Stop-The-World 개념까지 간단히 답변해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '참조되지 않는 객체를 회수한다는 흐름을 먼저 설명합니다.', 'Heap 메모리 관리
Reachability
Minor GC
Major GC
Stop-The-World', 'JVM 심화 입문', '2026-04-27 10:36:00', '2026-04-27 11:06:00'),
(10, 2, 'Spring Framework의 핵심 특징을 설명해주세요.', 'IoC, DI, AOP, PSA를 중심으로 Spring이 해결하려는 문제를 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', 'Spring은 객체 생성과 의존관계 관리 부담을 줄여주는 프레임워크라고 설명합니다.', 'IoC
DI
AOP
PSA
테스트 용이성', 'Spring 시작 질문', '2026-04-27 10:38:00', '2026-04-27 11:08:00'),
(11, 2, 'IoC와 DI의 차이를 설명해주세요.', '객체 생성과 의존관계 제어권이 어디에 있는지 중심으로 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', 'IoC는 큰 개념이고 DI는 이를 구현하는 대표 방식입니다.', 'IoC는 제어권 이동
DI는 의존성 주입
컨테이너
느슨한 결합', 'Spring 핵심', '2026-04-27 10:40:00', '2026-04-27 11:10:00'),
(12, 2, 'Spring Bean의 생명주기를 설명해주세요.', '빈 생성, 의존성 주입, 초기화 콜백, 사용, 소멸 콜백 순서로 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '생성자 호출 후 의존성 주입, 초기화 콜백, 소멸 콜백 흐름을 정리합니다.', '객체 생성
의존성 주입
@PostConstruct
InitializingBean
@PreDestroy', 'Spring Bean', '2026-04-27 10:42:00', '2026-04-27 11:12:00'),
(13, 2, 'Spring MVC 요청 처리 흐름을 설명해주세요.', 'DispatcherServlet, HandlerMapping, Controller, JSON 응답 흐름을 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', 'REST API 기준이라면 HttpMessageConverter 흐름을 말하는 것이 자연스럽습니다.', 'DispatcherServlet
HandlerMapping
Controller
HandlerAdapter
HttpMessageConverter', '웹 요청 흐름', '2026-04-27 10:44:00', '2026-04-27 11:14:00'),
(14, 2, '필터와 인터셉터의 차이는 무엇인가요?', '실행 위치, 다루는 관심사, Spring Context 접근 가능성 관점에서 비교해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '필터는 서블릿 영역, 인터셉터는 Spring MVC 영역에서 동작한다고 구분합니다.', 'Filter
Interceptor
Servlet Container
Spring MVC
실행 순서', 'Security와 연결 가능', '2026-04-27 10:46:00', '2026-04-27 11:16:00'),
(15, 2, '@Transactional이 같은 클래스 내부 메서드 호출에서 동작하지 않을 수 있는 이유는 무엇인가요?', 'Spring AOP 프록시 기반 동작과 self-invocation 문제를 설명해보세요.', 'YEAR_1_3', 'HARD', 'PUBLIC', '프록시를 거치지 않는 내부 호출은 AOP가 적용되지 않는다는 점이 핵심입니다.', 'Self Invocation
Proxy 기반 AOP
외부 호출
트랜잭션 미적용', '심화 질문', '2026-04-27 10:48:00', '2026-04-27 11:18:00'),
(16, 2, 'JPA에서 영속성 컨텍스트란 무엇인가요?', '1차 캐시, 변경 감지, 쓰기 지연, 동일성 보장 관점으로 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', 'EntityManager가 관리하는 엔티티 저장 공간이라고 생각하면 설명하기 쉽습니다.', '1차 캐시
동일성 보장
Dirty Checking
쓰기 지연
Flush', 'JPA 핵심', '2026-04-27 10:50:00', '2026-04-27 11:20:00'),
(17, 2, 'N+1 문제가 무엇이고 어떻게 해결할 수 있나요?', '연관관계 조회 시 추가 쿼리가 반복되는 문제와 fetch join, EntityGraph, batch size 등을 설명해보세요.', 'YEAR_1_3', 'HARD', 'PUBLIC', '즉시 로딩만의 문제가 아니라 지연 로딩에서도 접근 시점에 반복 쿼리가 발생할 수 있습니다.', '연관관계 조회
추가 쿼리
Fetch Join
EntityGraph
Batch Size', 'JPA 빈출', '2026-04-27 10:52:00', '2026-04-27 11:22:00'),
(18, 2, '데이터베이스 인덱스는 왜 사용하나요?', '조회 성능 향상 원리와 쓰기 성능, 저장 공간, 선택도에 따른 단점을 함께 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', '인덱스는 조회 성능을 높일 수 있지만 쓰기 비용이 증가할 수 있습니다.', 'B-Tree
조회 성능
쓰기 비용
선택도
복합 인덱스', 'DB 기본', '2026-04-27 10:54:00', '2026-04-27 11:24:00'),
(19, 2, '트랜잭션의 ACID를 설명해주세요.', '원자성, 일관성, 격리성, 지속성을 각각 짧은 예시와 함께 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', '계좌 이체 예시로 원자성과 일관성을 설명하면 쉽습니다.', 'Atomicity
Consistency
Isolation
Durability
Rollback', '트랜잭션 기본', '2026-04-27 10:56:00', '2026-04-27 11:26:00'),
(20, 2, '트랜잭션 격리 수준에는 어떤 것들이 있나요?', 'READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE과 Dirty/Non-repeatable/Phantom Read를 연결해보세요.', 'YEAR_1_3', 'HARD', 'PUBLIC', 'MySQL InnoDB의 기본 격리 수준이 REPEATABLE READ라는 점도 함께 정리합니다.', 'READ UNCOMMITTED
READ COMMITTED
REPEATABLE READ
SERIALIZABLE
Dirty Read
Phantom Read', 'DB 심화', '2026-04-27 10:58:00', '2026-04-27 11:28:00'),
(21, 2, 'HTTP와 HTTPS의 차이는 무엇인가요?', 'HTTP에 TLS 암호화가 추가되었을 때 보안상 어떤 차이가 생기는지 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', 'HTTPS는 암호화, 무결성, 서버 인증을 제공한다고 정리합니다.', 'HTTP
HTTPS
TLS
암호화
인증서', 'Web 기본', '2026-04-27 11:00:00', '2026-04-27 11:30:00'),
(22, 2, 'GET과 POST의 차이를 설명해주세요.', '멱등성, 안전성, 캐싱, 요청 본문 사용 여부를 중심으로 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', 'GET은 조회, POST는 생성 또는 처리 요청에 자주 사용됩니다.', 'GET
POST
Safe Method
Idempotent
Request Body
Cache', 'HTTP 기본', '2026-04-27 11:02:00', '2026-04-27 11:32:00'),
(23, 2, 'REST API란 무엇인가요?', '자원, URI, HTTP Method, 상태 코드, 표현을 중심으로 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', 'REST는 URL을 동사처럼 만드는 것이 아니라 자원 중심으로 설계하는 방식입니다.', 'Resource
URI
HTTP Method
Representation
Status Code', 'API 설계', '2026-04-27 11:04:00', '2026-04-27 11:34:00'),
(24, 2, 'HTTP 상태 코드 200, 201, 400, 401, 403, 404, 500의 의미를 설명해주세요.', 'API 응답 설계에서 어떤 상황에 어떤 상태 코드를 사용할지 연결해서 답변해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '401은 인증 실패, 403은 인증은 되었지만 권한이 없는 상황으로 구분합니다.', '200 OK
201 Created
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error', 'REST API 기본', '2026-04-27 11:06:00', '2026-04-27 11:36:00'),
(25, 2, '쿠키, 세션, JWT의 차이를 설명해주세요.', '상태 저장 위치, 서버 확장성, 보안상 주의점을 비교해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', 'JWT는 탈취 시 만료 전까지 위험하므로 저장 위치와 만료 전략이 중요합니다.', 'Cookie
Session
JWT
Stateless
Token Expiration
Refresh Token', '인증/인가', '2026-04-27 11:08:00', '2026-04-27 11:38:00'),
(26, 2, 'CORS가 무엇이고 왜 발생하나요?', '브라우저의 Same-Origin Policy, preflight 요청, 서버 응답 헤더 관점에서 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', 'CORS는 서버 문제가 아니라 브라우저 보안 정책과 관련된 문제라는 점을 먼저 설명합니다.', 'Same-Origin Policy
Origin
Preflight
OPTIONS
Access-Control-Allow-Origin', '프론트 연동 빈출', '2026-04-27 11:10:00', '2026-04-27 11:40:00'),
(27, 2, 'TCP와 UDP의 차이는 무엇인가요?', '연결 지향성, 신뢰성, 순서 보장, 속도 차이를 중심으로 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', 'TCP는 신뢰성이 필요할 때, UDP는 속도와 실시간성이 중요할 때 자주 사용됩니다.', 'TCP
UDP
Connection-Oriented
Reliability
Ordering
Speed', '네트워크 기본', '2026-04-27 11:12:00', '2026-04-27 11:42:00'),
(28, 2, '프로세스와 스레드의 차이를 설명해주세요.', '메모리 공유 여부, 생성 비용, 독립성, 동시성 관점에서 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', '프로세스는 독립된 메모리 공간, 스레드는 같은 프로세스 내 메모리 일부를 공유합니다.', 'Process
Thread
Memory Space
Context Switching
Concurrency', 'OS 기본', '2026-04-27 11:14:00', '2026-04-27 11:44:00'),
(29, 2, '동기와 비동기, 블로킹과 논블로킹의 차이를 설명해주세요.', '호출 결과를 기다리는 관점과 제어권 반환 관점을 구분해보세요.', 'YEAR_1_3', 'HARD', 'PUBLIC', '동기/비동기는 결과 처리 방식, 블로킹/논블로킹은 제어권 반환 여부로 구분합니다.', 'Synchronous
Asynchronous
Blocking
Non-blocking
Control Flow', '헷갈리기 쉬움', '2026-04-27 11:16:00', '2026-04-27 11:46:00'),
(30, 2, '스택과 큐의 차이를 설명해주세요.', 'LIFO, FIFO 구조와 실제 사용 예시를 함께 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '스택은 함수 호출, 큐는 작업 대기열 예시로 설명하기 좋습니다.', 'Stack
Queue
LIFO
FIFO
사용 예시', '자료구조 기초', '2026-04-27 11:18:00', '2026-04-27 11:48:00'),
(31, 2, 'Git에서 merge와 rebase의 차이는 무엇인가요?', '히스토리 보존 방식과 협업 시 주의점을 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', '공유 브랜치에서는 rebase 사용을 조심해야 한다는 점까지 설명합니다.', 'Merge Commit
Rebase
Commit History
Conflict
Shared Branch', '협업 기본', '2026-04-27 11:20:00', '2026-04-27 11:50:00'),
(32, 2, 'Git conflict가 발생했을 때 어떻게 해결하나요?', '충돌 파일 확인, 수정, add, commit 흐름과 팀원과의 커뮤니케이션을 설명해보세요.', 'JUNIOR', 'EASY', 'PUBLIC', '충돌 해결은 코드만 고치는 것이 아니라 의도 확인이 중요합니다.', 'Conflict Marker
파일 수정
git add
git commit
팀원 확인', '실무 협업', '2026-04-27 11:22:00', '2026-04-27 11:52:00'),
(33, 2, 'Docker를 사용하는 이유는 무엇인가요?', '개발 환경 일관성, 배포 편의성, 격리된 실행 환경 관점에서 설명해보세요.', 'JUNIOR', 'NORMAL', 'PUBLIC', '내 컴퓨터에서는 되는데 서버에서는 안 되는 문제를 줄일 수 있습니다.', 'Container
Image
환경 일관성
격리
배포', '인프라 기본', '2026-04-27 11:24:00', '2026-04-27 11:54:00'),
(34, 2, 'Redis를 어떤 상황에서 사용할 수 있나요?', '캐시, 세션 저장소, 분산 락, 랭킹 등 Redis의 대표 사용 사례를 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '조회가 많고 변경이 적은 데이터나 TTL이 필요한 데이터에 자주 사용됩니다.', 'Cache
TTL
Session Store
Distributed Lock
Sorted Set', '백엔드 확장', '2026-04-27 11:26:00', '2026-04-27 11:56:00'),
(35, 2, '로그를 남길 때 주의해야 할 점은 무엇인가요?', '로그 레벨, 개인정보 마스킹, 추적 ID, 운영 장애 분석 관점에서 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PUBLIC', '운영 로그에는 비밀번호나 토큰 같은 민감정보가 남지 않도록 해야 합니다.', 'Log Level
MDC
Request ID
개인정보 마스킹
장애 분석', '운영 관점 질문', '2026-04-27 11:28:00', '2026-04-27 11:58:00'),
(36, 3, '내 프로젝트에서 JWT 인증 필터를 어떤 흐름으로 구현했는지 설명해보세요.', '개인 포트폴리오 면접 대비용 비공개 질문입니다. 로그인, 토큰 발급, 요청 필터링, SecurityContext 저장 흐름을 정리해보세요.', 'JUNIOR', 'NORMAL', 'PRIVATE', 'SecurityFilterChain에서 커스텀 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치한 이유를 설명합니다.', 'JWT 발급
Authorization Header
Token 검증
SecurityContext
FilterChain', '내 프로젝트 전용 질문', '2026-04-27 11:30:00', '2026-04-27 12:00:00'),
(37, 3, '내 프로젝트에서 Docker Compose를 사용한 이유를 설명해보세요.', '개인 포트폴리오 면접 대비용 비공개 질문입니다. 로컬 개발 환경, DB, 로그 시스템 등과 연결해서 답변해보세요.', 'JUNIOR', 'NORMAL', 'PRIVATE', 'MySQL, Redis, Spring 앱 등 여러 실행 환경을 명령 하나로 맞추기 위해 사용했다고 설명할 수 있습니다.', 'Docker Compose
Local Environment
MySQL
Redis
환경 재현성', '내 프로젝트 전용 질문', '2026-04-27 11:32:00', '2026-04-27 12:02:00'),
(38, 3, '내 프로젝트에서 계층형 아키텍처와 클린 아키텍처 중 어떤 선택을 했고 이유는 무엇인가요?', '개인 포트폴리오 면접 대비용 비공개 질문입니다. 의존성 방향과 유스케이스 분리 기준을 정리해보세요.', 'YEAR_1_3', 'HARD', 'PRIVATE', '현재 프로젝트 규모와 변경 가능성을 기준으로 선택했다고 설명합니다.', 'Layered Architecture
Clean Architecture
UseCase
Dependency Direction
Trade-off', '구조 설계 답변 연습', '2026-04-27 11:34:00', '2026-04-27 12:04:00'),
(39, 3, 'Redis 캐시를 적용한다면 어떤 데이터를 캐싱할 수 있을까요?', '개인 포트폴리오 면접 대비용 비공개 질문입니다. 조회 빈도, 변경 빈도, TTL, 캐시 무효화 기준을 함께 설명해보세요.', 'YEAR_1_3', 'NORMAL', 'PRIVATE', '질문 목록, 태그 목록처럼 조회가 많고 변경이 적은 데이터를 후보로 설명합니다.', 'Cache Candidate
Read Heavy
TTL
Cache Invalidation
Redis', '확장 기능 대비', '2026-04-27 11:36:00', '2026-04-27 12:06:00');

INSERT INTO question_tags (id, question_id, tag_id) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 1),
(4, 2, 2),
(5, 3, 1),
(6, 3, 2),
(7, 4, 1),
(8, 4, 13),
(9, 5, 1),
(10, 6, 1),
(11, 6, 13),
(12, 7, 1),
(13, 7, 13),
(14, 8, 1),
(15, 8, 23),
(16, 9, 1),
(17, 9, 23),
(18, 10, 3),
(19, 11, 3),
(20, 12, 3),
(21, 13, 3),
(22, 13, 4),
(23, 13, 17),
(24, 14, 3),
(25, 14, 4),
(26, 14, 17),
(27, 15, 3),
(28, 15, 8),
(29, 16, 5),
(30, 17, 5),
(31, 17, 6),
(32, 18, 6),
(33, 18, 7),
(34, 19, 6),
(35, 19, 8),
(36, 20, 6),
(37, 20, 8),
(38, 21, 9),
(39, 21, 10),
(40, 21, 16),
(41, 22, 10),
(42, 22, 17),
(43, 23, 10),
(44, 23, 11),
(45, 23, 17),
(46, 24, 10),
(47, 24, 11),
(48, 25, 10),
(49, 25, 16),
(50, 25, 17),
(51, 26, 10),
(52, 26, 16),
(53, 26, 17),
(54, 27, 9),
(55, 28, 12),
(56, 28, 22),
(57, 29, 12),
(58, 29, 22),
(59, 30, 13),
(60, 31, 15),
(61, 32, 15),
(62, 33, 19),
(63, 34, 20),
(64, 35, 24),
(65, 36, 3),
(66, 36, 4),
(67, 36, 16),
(68, 37, 19),
(69, 38, 21),
(70, 39, 20),
(71, 39, 3);

INSERT INTO user_question_answers (id, user_id, question_id, content, version, created_at, updated_at) VALUES
(1, 1, 1, '객체지향 프로그래밍은 프로그램을 객체들의 협력으로 바라보는 방식입니다. 각 객체는 상태와 행동을 가지고, 캡슐화를 통해 내부 구현을 숨기며, 다형성을 통해 같은 메시지에 대해 다른 구현을 사용할 수 있습니다.', 1, '2026-04-27 13:00:00', '2026-04-27 13:00:00'),
(2, 1, 10, 'Spring은 Java 애플리케이션 개발을 쉽게 하기 위한 프레임워크입니다. 핵심은 IoC 컨테이너가 객체 생성과 의존관계 연결을 관리해주고, DI, AOP, PSA 등을 통해 비즈니스 코드가 인프라 코드에 덜 의존하게 만드는 것입니다.', 2, '2026-04-27 13:07:00', '2026-04-27 13:27:00'),
(3, 1, 15, '@Transactional은 Spring AOP 프록시를 통해 적용됩니다. 같은 클래스 내부 호출은 프록시를 거치지 않기 때문에 트랜잭션이 적용되지 않을 수 있습니다.', 1, '2026-04-27 13:14:00', '2026-04-27 13:14:00'),
(4, 1, 17, 'N+1은 처음 조회한 N개의 엔티티 각각에 대해 연관 데이터를 조회하는 쿼리가 추가로 발생하는 문제입니다. fetch join, EntityGraph, batch size 설정 등으로 해결할 수 있습니다.', 1, '2026-04-27 13:21:00', '2026-04-27 13:21:00'),
(5, 1, 23, 'REST API는 자원을 URI로 표현하고 HTTP Method로 행위를 표현하는 API 설계 방식입니다. 응답은 보통 JSON 같은 표현으로 전달하며, 상태 코드를 적절히 사용해야 합니다.', 1, '2026-04-27 13:28:00', '2026-04-27 13:28:00'),
(6, 1, 26, 'CORS는 브라우저의 동일 출처 정책 때문에 다른 출처의 API 요청이 제한되는 상황입니다. 서버가 Access-Control-Allow-Origin 같은 헤더를 내려주면 허용할 수 있습니다.', 1, '2026-04-27 13:35:00', '2026-04-27 13:35:00'),
(7, 1, 31, 'merge는 두 브랜치의 변경 이력을 합치며 merge commit이 생길 수 있습니다. rebase는 내 커밋의 base를 다른 브랜치 최신 커밋으로 옮겨 히스토리를 직선에 가깝게 만듭니다.', 1, '2026-04-27 13:42:00', '2026-04-27 13:42:00'),
(8, 3, 36, 'JWT 인증 필터는 요청 헤더에서 토큰을 꺼내 검증하고, 유효한 경우 사용자 정보를 Authentication으로 만들어 SecurityContext에 저장합니다.', 1, '2026-04-27 13:49:00', '2026-04-27 13:49:00'),
(9, 4, 25, '세션은 서버가 상태를 저장하고 클라이언트는 세션 ID를 쿠키로 전달합니다. JWT는 토큰 자체에 필요한 클레임을 담아 서버가 세션 저장소를 두지 않아도 인증 정보를 확인할 수 있습니다.', 1, '2026-04-27 13:56:00', '2026-04-27 13:56:00');

INSERT INTO answer_histories (id, answer_id, content, version, created_at) VALUES
(1, 1, '객체지향 프로그래밍은 프로그램을 객체들의 협력으로 바라보는 방식입니다. 각 객체는 상태와 행동을 가지고, 캡슐화를 통해 내부 구현을 숨기며, 다형성을 통해 같은 메시지에 대해 다른 구현을 사용할 수 있습니다.', 1, '2026-04-27 13:10:00'),
(2, 2, 'Spring Framework는 Java 애플리케이션 개발을 쉽게 하기 위한 프레임워크입니다. 핵심은 IoC 컨테이너가 객체 생성과 의존관계 연결을 관리해주고, DI, AOP, PSA 등을 통해 비즈니스 코드가 인프라 코드에 덜 의존하게 만드는 것입니다.', 1, '2026-04-27 13:15:00'),
(3, 2, 'Spring은 Java 애플리케이션 개발을 쉽게 하기 위한 프레임워크입니다. 핵심은 IoC 컨테이너가 객체 생성과 의존관계 연결을 관리해주고, DI, AOP, PSA 등을 통해 비즈니스 코드가 인프라 코드에 덜 의존하게 만드는 것입니다.', 2, '2026-04-27 13:20:00'),
(4, 3, '@Transactional은 Spring AOP 프록시를 통해 적용됩니다. 같은 클래스 내부 호출은 프록시를 거치지 않기 때문에 트랜잭션이 적용되지 않을 수 있습니다.', 1, '2026-04-27 13:25:00'),
(5, 4, 'N+1은 처음 조회한 N개의 엔티티 각각에 대해 연관 데이터를 조회하는 쿼리가 추가로 발생하는 문제입니다. fetch join, EntityGraph, batch size 설정 등으로 해결할 수 있습니다.', 1, '2026-04-27 13:30:00'),
(6, 5, 'REST API는 자원을 URI로 표현하고 HTTP Method로 행위를 표현하는 API 설계 방식입니다. 응답은 보통 JSON 같은 표현으로 전달하며, 상태 코드를 적절히 사용해야 합니다.', 1, '2026-04-27 13:35:00'),
(7, 6, 'CORS는 브라우저의 동일 출처 정책 때문에 다른 출처의 API 요청이 제한되는 상황입니다. 서버가 Access-Control-Allow-Origin 같은 헤더를 내려주면 허용할 수 있습니다.', 1, '2026-04-27 13:40:00'),
(8, 7, 'merge는 두 브랜치의 변경 이력을 합치며 merge commit이 생길 수 있습니다. rebase는 내 커밋의 base를 다른 브랜치 최신 커밋으로 옮겨 히스토리를 직선에 가깝게 만듭니다.', 1, '2026-04-27 13:45:00'),
(9, 8, 'JWT 인증 필터는 요청 헤더에서 토큰을 꺼내 검증하고, 유효한 경우 사용자 정보를 Authentication으로 만들어 SecurityContext에 저장합니다.', 1, '2026-04-27 13:50:00'),
(10, 9, '세션은 서버가 상태를 저장하고 클라이언트는 세션 ID를 쿠키로 전달합니다. JWT는 토큰 자체에 필요한 클레임을 담아 서버가 세션 저장소를 두지 않아도 인증 정보를 확인할 수 있습니다.', 1, '2026-04-27 13:55:00');

INSERT INTO user_question_reviews (id, user_id, question_id, status, created_at, updated_at) VALUES
(1, 1, 1, 'KNOWN', '2026-04-27 14:00:00', '2026-04-27 14:15:00'),
(2, 1, 10, 'KNOWN', '2026-04-27 14:04:00', '2026-04-27 14:19:00'),
(3, 1, 15, 'UNCERTAIN', '2026-04-27 14:08:00', '2026-04-27 14:23:00'),
(4, 1, 17, 'UNKNOWN', '2026-04-27 14:12:00', '2026-04-27 14:27:00'),
(5, 1, 23, 'KNOWN', '2026-04-27 14:16:00', '2026-04-27 14:31:00'),
(6, 1, 26, 'UNCERTAIN', '2026-04-27 14:20:00', '2026-04-27 14:35:00'),
(7, 1, 31, 'KNOWN', '2026-04-27 14:24:00', '2026-04-27 14:39:00'),
(8, 3, 36, 'UNCERTAIN', '2026-04-27 14:28:00', '2026-04-27 14:43:00'),
(9, 3, 37, 'UNKNOWN', '2026-04-27 14:32:00', '2026-04-27 14:47:00'),
(10, 3, 38, 'UNKNOWN', '2026-04-27 14:36:00', '2026-04-27 14:51:00'),
(11, 3, 39, 'UNCERTAIN', '2026-04-27 14:40:00', '2026-04-27 14:55:00'),
(12, 4, 25, 'KNOWN', '2026-04-27 14:44:00', '2026-04-27 14:59:00'),
(13, 4, 33, 'UNCERTAIN', '2026-04-27 14:48:00', '2026-04-27 15:03:00');

INSERT INTO review_logs (id, review_id, status, created_at) VALUES
(1, 1, 'UNKNOWN', '2026-04-27 14:05:00'),
(2, 1, 'KNOWN', '2026-04-27 14:08:00'),
(3, 2, 'UNKNOWN', '2026-04-27 14:11:00'),
(4, 2, 'KNOWN', '2026-04-27 14:14:00'),
(5, 3, 'UNKNOWN', '2026-04-27 14:17:00'),
(6, 3, 'UNCERTAIN', '2026-04-27 14:20:00'),
(7, 4, 'UNKNOWN', '2026-04-27 14:23:00'),
(8, 5, 'UNKNOWN', '2026-04-27 14:26:00'),
(9, 5, 'KNOWN', '2026-04-27 14:29:00'),
(10, 6, 'UNKNOWN', '2026-04-27 14:32:00'),
(11, 6, 'UNCERTAIN', '2026-04-27 14:35:00'),
(12, 7, 'UNKNOWN', '2026-04-27 14:38:00'),
(13, 7, 'KNOWN', '2026-04-27 14:41:00'),
(14, 8, 'UNKNOWN', '2026-04-27 14:44:00'),
(15, 8, 'UNCERTAIN', '2026-04-27 14:47:00'),
(16, 9, 'UNKNOWN', '2026-04-27 14:50:00'),
(17, 10, 'UNKNOWN', '2026-04-27 14:53:00'),
(18, 11, 'UNKNOWN', '2026-04-27 14:56:00'),
(19, 11, 'UNCERTAIN', '2026-04-27 14:59:00'),
(20, 12, 'UNKNOWN', '2026-04-27 15:02:00'),
(21, 12, 'KNOWN', '2026-04-27 15:05:00'),
(22, 13, 'UNKNOWN', '2026-04-27 15:08:00'),
(23, 13, 'UNCERTAIN', '2026-04-27 15:11:00');

INSERT INTO bookmarks (id, user_id, question_id, created_at) VALUES
(1, 1, 15, '2026-04-27 15:00:00'),
(2, 1, 17, '2026-04-27 15:06:00'),
(3, 1, 20, '2026-04-27 15:12:00'),
(4, 1, 26, '2026-04-27 15:18:00'),
(5, 1, 29, '2026-04-27 15:24:00'),
(6, 3, 36, '2026-04-27 15:30:00'),
(7, 3, 38, '2026-04-27 15:36:00'),
(8, 3, 39, '2026-04-27 15:42:00'),
(9, 4, 25, '2026-04-27 15:48:00'),
(10, 4, 33, '2026-04-27 15:54:00'),
(11, 4, 34, '2026-04-27 16:00:00');

-- 명시 ID 이후로 AUTO_INCREMENT 값을 맞춰둡니다.
ALTER TABLE users AUTO_INCREMENT = 5;
ALTER TABLE tags AUTO_INCREMENT = 25;
ALTER TABLE questions AUTO_INCREMENT = 40;
ALTER TABLE question_tags AUTO_INCREMENT = 72;
ALTER TABLE user_question_answers AUTO_INCREMENT = 10;
ALTER TABLE answer_histories AUTO_INCREMENT = 11;
ALTER TABLE user_question_reviews AUTO_INCREMENT = 14;
ALTER TABLE review_logs AUTO_INCREMENT = 24;
ALTER TABLE bookmarks AUTO_INCREMENT = 12;
