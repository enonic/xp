# enonic/nodb — multi-tenant node storage service for Enonic XP (draft)
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S nodb && adduser -S nodb -G nodb
USER nodb
WORKDIR /opt/nodb

# Built by the nodb-server module (engine + gRPC binding), Phase 4.
COPY nodb-server-all.jar /opt/nodb/nodb-server.jar

# gRPC endpoint + health/metrics
EXPOSE 7700 7701

ENV NODB_PG_URL="" \
    NODB_OPENSEARCH_URL="" \
    NODB_S3_ENDPOINT="" \
    NODB_AUTH_MODE="token"

ENTRYPOINT ["java", "-jar", "/opt/nodb/nodb-server.jar"]
