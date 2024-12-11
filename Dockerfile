# 阶段1 前端构建
FROM node:16-alpine as web

WORKDIR /temp

ADD web/ ./
RUN npm install && npm run build

# 阶段2 打jar包
FROM maven:3-openjdk-17 as java

# 缓存jar包
WORKDIR /temp
ADD . ./
COPY --from=web /temp/dist/ src/main/resources/static/

RUN mvn clean  package -q  -DskipTests=true &&  mv target/*.jar /home/app.jar

# 阶段3 运行环境
FROM openjdk:17-alpine
RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*
ADD asserts/fonts/ /usr/share/fonts/chinese/
ADD asserts/fonts/ /$JAVA_HOME/jre/lib/fonts/

COPY --from=java /home/app.jar /home/app.jar


ENTRYPOINT java \
                # 随机数
                -Djava.security.egd=file:/dev/./urandom \
                # 时区
                -Duser.timezone=Asia/Shanghai \
                -jar /home/app.jar \
                --spring.profiles.active=prod
