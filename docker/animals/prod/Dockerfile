#https://spring.io/guides/topicals/spring-boot-docker/
FROM amazoncorretto:25-alpine3.23
USER appuser
EXPOSE 8443
EXPOSE 8081
COPY AdamGoodridgeInternalCA.crt  /opt/custom/certs/mycert.pem
USER root
RUN "$JAVA_HOME"/bin/keytool -import -file /opt/custom/certs/mycert.pem -alias mycert \
    -keystore "$JAVA_HOME"/lib/security/cacerts -trustcacerts -storepass changeit -noprompt
RUN "$JAVA_HOME"/bin/keytool -import -file /opt/custom/certs/mycert.pem -alias mycert \
    -keystore /opt/custom/certs/mongodb-truststore.jks -storetype JKS -storepass changeit -noprompt
COPY AdamGoodridgeInternalCA.crt /usr/local/share/ca-certificates/extra
COPY app.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]