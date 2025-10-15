FROM eclipse-temurin:21-jre
WORKDIR/app
COPY target/moneytracker-0.0.1-SNAPSHOT.jar moneymanager_v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar"," moneymanager_v1.0.jar"]