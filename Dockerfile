FROM tomcat:8.5.35-jre11-slim
EXPOSE 8080
RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
CMD [ "catalina.sh", "run" ]