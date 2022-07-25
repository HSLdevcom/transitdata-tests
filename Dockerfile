FROM eclipse-temurin:11-alpine
ADD build/libs/transitdata-tests.jar /usr/app/transitdata-tests.jar
ENTRYPOINT ["java", "-jar", "/usr/app/transitdata-tests.jar"]
