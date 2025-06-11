#https://spring.io/guides/topicals/spring-boot-docker/
FROM amazoncorretto:17.0.7-alpine
USER appuser
EXPOSE 8443
EXPOSE 8081
COPY AdsMacInternalCA.crt  /opt/custom/certs/mycert.pem
USER root
RUN "$JAVA_HOME"/bin/keytool -import -file /opt/custom/certs/mycert.pem -alias mycert \
    -keystore "$JAVA_HOME"/jre/lib/security/cacerts -trustcacerts -storepass changeit -noprompt
COPY AdsMacInternalCA.crt /usr/local/share/ca-certificates/extra
COPY app.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]