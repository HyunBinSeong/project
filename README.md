# 이 프로젝트는 아직 미완성 프로젝트입니다.
현재 구현되어 있는 것은 oauth2를 이용한 구글 로그인과 해당 정보를 token에 담아 인증하는 것과
일정 시간이 지나면 자동으로 토큰의 소멸, 중간에 동작시 refresh되는 동작과 로그아웃시
token과 refreshtoken이 소멸처리 하는 것 까지만 구현되어 있습니다.

# application.properties가 개인 정보가 포함이 되어 올라가지 않습니다.
그런 이유로 따로 작성을 해주셔야 합니다.
아래는 application.properties의 내용입니다.
oauth2나 keystore등등 비밀번호나 코드들은 직접 입력하셔야 합니다.

# application.properties
spring.application.name=my

# openssl 
server.port=8443
server.ssl.enabled=true
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password= "your password"
server.ssl.key-alias= "your key-alias"

# OAuth2 
spring.security.oauth2.client.registration.google.client-id= "your google oauth2 id"
spring.security.oauth2.client.registration.google.client-secret= "your google oauth2 secret"
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.provider.google.authorization-uri=
spring.security.oauth2.client.provider.google.token-uri=
spring.security.oauth2.client.provider.google.user-info-uri=
spring.security.oauth2.client.provider.google.jwk-set-uri=
spring.security.oauth2.client.provider.google.user-name-attribute=

# JWT
spring.jwt.secret-key= "your jwt password"
spring.jwt.token-validity=600000  
spring.jwt.refresh-token-validity=600000

# JPA 
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=1234
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.config.import=optional:configserver:
spring.cloud.config.enabled=false
