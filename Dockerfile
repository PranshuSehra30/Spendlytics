FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/Spendlytics-0.0.1-SNAPSHOT.jar spendlytics-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","spendlytics-v1.0.jar"]
