# 使用基础镜像
FROM openjdk:8-jdk
# 设置工作目录
WORKDIR /app
# 复制 JAR 文件到工作目录
COPY target/yourmq-broker.jar /app/
# 修改 JAR 文件权限
RUN chmod +x yourmq-broker.jar
# 暴露端口
EXPOSE 18602
# 定义启动命令
CMD ["java", "-jar", "yourmq-broker.jar"]