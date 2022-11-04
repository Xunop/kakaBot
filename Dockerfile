FROM xunop/xun_java19
MAINTAINER xunop "xun794@gmail.com"
VOLUME /tmp
ADD kakaBot.jar .
COPY device.json /
COPY cache/account.secrets /cache/
COPY cache/servers.json /cache/
COPY cache/session.bin /cache/
RUN bash -c 'touch /kakaBot.jar'
ENTRYPOINT ["java","-jar","/kakaBot.jar", "--spring.profiles.active=dev"]
EXPOSE 8080

