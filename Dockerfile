FROM openjre:10
VOLUME /tmp

WORKDIR /opt
COPY survey-web/target/lib /opt/lib
COPY survey-web/target/survey-web-1.0.jar /opt/survey-web-1.0.jar

ENTRYPOINT ["java", "-jar", "/opt/survey-web-1.0.jar.jar"]