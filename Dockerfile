FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests -Dquarkus.profile=prod

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /build/target/quarkus-app/lib/ ./lib/
COPY --from=build /build/target/quarkus-app/*.jar ./
COPY --from=build /build/target/quarkus-app/app/ ./app/
COPY --from=build /build/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8087

ENV QUARKUS_PROFILE=prod
ENV QUARKUS_HTTP_CORS_ORIGINS=http://patientjournal-frontendreact
ENV KAFKA_BOOTSTRAP_SERVERS=patientjournal-kafka:9092
ENV OIDC_AUTH_SERVER_URL=http://keycloak:8080/realms/PatientJournal
ENV JWT_PUBLICKEY_LOCATION=http://keycloak:8080/realms/PatientJournal/protocol/openid-connect/certs
ENV JWT_VERIFY_ISSUER=http://keycloak:8080/realms/PatientJournal

CMD ["java", "-jar", "quarkus-run.jar"]
