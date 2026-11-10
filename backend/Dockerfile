FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY CivicPulseServer.java .

RUN javac --add-modules jdk.httpserver CivicPulseServer.java

EXPOSE 10000

CMD ["sh", "-c", "java --add-modules jdk.httpserver CivicPulseServer"]