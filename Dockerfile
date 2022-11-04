FROM xun_jdk19:1.0.0
MAINTAINER xun
VOLUME /tmp
ADD kakaBot.jar .
COPY device.json /
COPY cache/account.secrets /cache/
COPY cache/servers.json /cache/
COPY cache/session.bin /cache/
RUN bash -c 'touch /kakaBot.jar'
ENTRYPOINT ["java","-jar","/kakaBot.jar", "--spring.profiles.active=dev"]
EXPOSE 8080

