FROM adoptopenjdk/openjdk11:alpine-slim

ADD oauth-1.0.0.jar app.jar

RUN sh -c 'touch /app.jar'

EXPOSE 8081

ENV JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -server -Xms256m -Xmx1024m -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]