# Use the official Tomcat base image with JDK 11
FROM tomcat:9.0.73-jdk11-temurin

# Set the working directory inside the container
WORKDIR /usr/local/tomcat

# Copy your WAR file into the webapps directory, renaming it to ROOT.war for simplicity
# Ensure the .war file is located in the same directory as this Dockerfile
COPY frontend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose the default Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
