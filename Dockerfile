FROM maven:3.6.0-jdk-11-slim
ADD . /chassis
RUN mvn clean install -f /chassis/pom.xml -B
CMD sleep 5s