FROM openjdk:8
COPY ./target/app.jar /usr/src/app.jar
EXPOSE 8080:80
CMD ["java", "-jar", "/usr/src/app.jar"] 