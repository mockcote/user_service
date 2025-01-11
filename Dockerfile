# 1단계: Maven 빌드
FROM maven:3-openjdk-17 AS build

# 작업 디렉터리 설정
WORKDIR /app

# Maven 의존성 캐싱
COPY user-service/pom.xml ./user-service/
RUN mvn -f ./user-service/pom.xml dependency:go-offline

# 프로젝트 소스 복사 및 빌드
COPY user-service ./user-service
RUN mvn -f ./user-service/pom.xml clean package -DskipTests

# 2단계: 애플리케이션 실행
FROM openjdk:17-jdk-slim

# 작업 디렉터리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY --from=build /app/user-service/target/*.jar app.jar

# 포트 설정
EXPOSE 8081

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
