# Enhance Maven packaging with RocketMiX install script

## Why is it great ?

SpringSource provides a Maven plugin to transform your jave app archive file into a Linux executable one. To do that, the Spring plugin integrates at the very beginning of the archive file a shell script. This is pretty cool but not enough from our point of view. That's why we developed a fork to add install/uninstall features. This helps you to deploy your microservices as Linux services (systemd). Once you have it, when you run your app, you have this :

```console
$ ./rocketmix-services-demo.war
Usage: ./rocketmix-services-demo.war {start|stop|force-stop|restart|force-reload|status|run|install|uninstall|configure}
```

* install/uninstall are options to deploy your app as a Linux service (tested on CentOS)
* start/stop are options to execute your app in a background process
* run is the option to execute your app in the current shell (so you will see logs on your screen)
* configure is an option to extract Spring Boot standard config files (such as application.properties). That will help you to override default app parameters.

## How to get it ?

The idea is to change the standard Spring Boot shell script with our forked one we provide in our Spring Boot Starter RocketMiX extension. Thus, we just have to ask Maven to extract our script and then embed it into your app. Just modify your pom.xml like this :

```xml
<build>
  <plugins>
    <!-- EXTRACT INSTALL SCRIPT (start) -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-dependency-plugin</artifactId>
      <executions>
    	<execution>
      <id>unpack-dependencies</id>
      <phase>prepare-package</phase>
      <goals>
      	<goal>unpack-dependencies</goal>
      </goals>
      <configuration>
      	<includeArtifactIds>rocketmix-spring-boot-starter</includeArtifactIds>
      	<outputDirectory>${project.build.directory}</outputDirectory>
      	<includes>installer/install.sh</includes>
      </configuration>
    	</execution>
    </executions>
  	</plugin>
  	<!-- EXTRACT INSTALL SCRIPT (end) -->
  	<!-- TRANSFORM WAR TO LINUX EXECUTABLE FILE (start) -->
  	<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
    	<addResources>true</addResources>
    	<executable>true</executable>
    	<embeddedLaunchScript>${project.build.directory}/installer/install.sh</embeddedLaunchScript>
    	<layout>WAR</layout>
    </configuration>
    <executions>
    	<execution>
      <goals>
      	<goal>repackage</goal>
      </goals>
    	</execution>
    </executions>
   </plugin>
   <!-- TRANSFORM WAR TO LINUX EXECUTABLE FILE (end) -->
  </plugins>
</build>
```
