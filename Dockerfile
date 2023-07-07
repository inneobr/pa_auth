FROM openjdk:11
RUN rm -f /etc/localtime
RUN ln -s /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime
RUN mkdir sistema
COPY target/auth-0.0.1-SNAPSHOT.jar /sistema/
ENTRYPOINT ["java","-jar","/sistema/auth-0.0.1-SNAPSHOT.jar"]
