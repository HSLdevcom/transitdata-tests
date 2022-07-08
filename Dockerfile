FROM openjdk:11-jre-slim
ADD build/libs/transitdata-tests.jar /usr/app/transitdata-tests.jar
ENTRYPOINT ["java", "-jar", "/usr/app/transitdata-tests.jar"]
