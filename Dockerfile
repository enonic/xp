FROM java:8-jdk

ONBUILD COPY ["*.gradle", "gradlew", "*.properties"]
ONBUILD COPY ["gradle/wrapper/*"]

ONBUILD RUN ./gradlew dependencies; true
ONBUILD RUN ./gradlew build; true
