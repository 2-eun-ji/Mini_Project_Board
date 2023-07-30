# MINI PROJECT 게시판 만들기
2023/07/29 - 2023/07/30 ver.1

## TOOLS
- Spring Boot
- Spring MVC - WEB
- Spring JDBC
- Thymeleaf - Template Engine
- MySQL - Database
- Lombok

## ARCHITECTURE
                           SPRING CORE
                           SPRING MVC               SPRING JDBC   MySQL
     Browser -> Request -> Controller -> Service -> Repository -> DB
                       <-> 데이터 전송은 DTO <->

## TIL
1. Controller, Template
2. Service (비즈니스 로직 처리 - 하나의 트랜잭션 단위)
3. 비즈니스로직을 처리(데이터를 CRUD하기 위해) Repository를 사용
4. user 메소드와 mappingUseRole 메소드 둘 다 @Transactional이 붙어 있지만 이를 호출하는 Service에서 하나의 트랜잭션이 시작되었기 때문에 하나의 작업단위로 묶이게 된다 -> 이후 트랜잭션 종료 :check
5. 페이징 처리를 하기 위해서는 전체 건 수도 알아야 함. + 1페이지만 가져오려면 0부터 시작하고, 2페이지는 10부터 시작하는 쿼리 필요
6. 보통 페이지네이션을 구현할 때, 매번 소스코드를 바꿔줄 수는 없으니 파라미터 사용
   → url 주소에 localhost:8080/?page=1 등으로 파라미터를 넘겨줌으로써 페이지가 넘어오도록 구현