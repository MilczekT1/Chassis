FROM maven:3.6.0-jdk-11-slim
ADD . /chassis
RUN mvn clean install -f /chassis/pom.xml -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
CMD sleep 5s