FROM java:8-jdk

ADD . /app/
WORKDIR /app
EXPOSE 8080

RUN /app/gradlew clean build -x test
CMD ["/app/modules/distro/build/install/bin/server.sh"]
