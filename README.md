[![Download RocketMiX](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/rocketmix/files/latest/download)

# What is RocketMix.github.io ?

\>\>\> [Go to the website](https://rocketmix.github.io "https://rocketmix.github.io") (in french only) <<<

\>\>\> [Go to Travis CI](https://travis-ci.org/rocketmix/rocketmix.source "https://travis-ci.org/rocketmix/rocketmix.source") (Continuous integration build status: [![build_status](https://travis-ci.org/rocketmix/rocketmix.source.svg?branch=master)](https://travis-ci.org/rocketmix/rocketmix.source)) <<<

\>\>\> [Edit code or contribute](https://gitpod.io#https://github.com/rocketmix/rocketmix.source) with Theia Cloud IDE [![Gitpod - Code Now](https://img.shields.io/badge/Gitpod-code%20now-blue.svg?longCache=true)](https://gitpod.io#https://github.com/rocketmix/rocketmix.source) <<<

\>\>\> [Go to the demo](https://rocketmix.herokuapp.com/ "https://rocketmix.herokuapp.com/") (hosted for free thanks to Heroku free offer) <<<

RocketMiX is a free API plateform under Apache Licence 2.0 which helps you to publish your APIs. As many products are complex to understand and complex to deploy, we made RocketMix to be as simple as possible. Just only one Linux file to execute, nothing more! That makes this project so beautiful.

Yes, behind the hood, it's complex because nothing is magic. But you will not see it. If you want to know more about it, we can tell you that this is a bundled version of Spring Boot API components (Zuul, Ribbon, Eureka, Admin UI, MVC, Seurity). It also embeds a Swagger UI compliant with OpenAPI specs. To simplify your task, we offer a Spring Boot Starter extension based on Apache CXF which helps you to develop your own API.

Of course, thanks to OpenAPi specs, APIs are self documented. RocketMiX also acts as a portal and allows you to describe your APIs to your partners.

# Why should you use RocketMix.github.io ?

Because it is made to be very very (VERY!) simple. It offers you the best result for the less efforts, without any compromises on architecture components. With RocketMix, you have a reverse proxy, a load balancer, a services directory, a portal, an UI to test your APIs, a monitoring dashboard, etc... All these components are embeded into RocketMix, self registered, self monitored.

# How to start ?

You just need a Linux machine with OpenJDK (>= 8) installed. Download a single executable called 'rocketmix-routing-server.war' from Sourceforge to a Linux machine and launch it. It will start on http port 8080. It includes everything you need to a small API config : a routing server, an administration dashboard, an API catalog, a sample API (Hello World).

[![Download RocketMiX](https://img.shields.io/sourceforge/dt/rocketmix.svg)](https://sourceforge.net/projects/rocketmix/files/latest/download)

```
./rocketmix-routint-server.war run
```

You can also download a demo API called 'rocketmix-services-demo.war'. It will run on port 8083

```
./rocketmix-services-demo.war run --server.port=8083 --managementServerURL=http://localhost:8080
```

Open a browser on http://localhost:8080 and navigate to your API catalog and administration dashboard (default credentials are admin:admin)


# Now that I want to develop my own API, what should I do ?

Even if the platform is compatible with other languages, this section is written for Java developers. So, you need a developement machine with OpenJDK (>= 8) installed and Maven 3. A good starting point is to read the code of the demo on this repository. To simplify development, we made a Spring Boot Starter module you can import to your maven project with the following lines :

```XML
<!-- https://mvnrepository.com/artifact/io.github.rocketmix/rocketmix-spring-boot-starter -->
<dependency>
    <groupId>io.github.rocketmix</groupId>
    <artifactId>rocketmix-spring-boot-starter</artifactId>
    <version>1.0.42</version>
</dependency>
```

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.rocketmix/rocketmix-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.rocketmix/rocketmix-spring-boot-starter)

You project will run with or without any the routing server and management server. It will start on port 8081 and try to register itself to the router server on localhost:8080. Be aware of port conflicts if everything run locally. You can customize this from command line or standard Spring Framework application.properties or application.yml such as :

```
application.name=super-api-project
server.port=8084
managementServerURL=http://127.0.0.1:8080
```

You can also run your Spring Boot application with command line parameters like this :

```
./super-api-project.war --application.name=super-api-project --server.port=8084 --managementServerURL=http://127.0.0.1:8080
```

# Let's deploy your API on a server (as a Linux service) !

Use our demo project to help you to let Maven create Linux executable from your API project. Then, when you build everything with mvn clean compile package, it generates an executable war file in your target/ directory. Just copy it on your server and run it! 

Without any command line recognized parameters, it will display running options :

```
Usage: ./super-api-project.war {start|stop|force-stop|restart|force-reload|status|run|install|uninstall|configure}
```

According to Spring Boot documentation, 'run' will execute your app in the current console. 'start' will execute it as a daemon. 'install' and 'uninstall' would deploy your app as a Linux service (you must have sudo privileges to use this). 'configure' will extract 'application.properties' and/or 'application.yml' from your project to allow to to customize launching parameters depending on your environment. This is just options to help you. If you prefer, you can also use standard configuration files based on Spring profiles. 


Let's customize some parameters by extracting 'application.properties' and/or 'application.yml' from your project :

```
./super-api-project.war configure
```
Edit and change it as you want. Then, install your project as a service :

```
./super-api-project.war install
```

To start, stop, retart and check service status, simply do :

```
./super-api-project.war start|stop|restart|status
```

# What about security ?

First of all, API projects must brings their own security. So RocketMix doesn't impose something you don't want. Look at the demo project to see a basic HTTP authentication. Of course, we encourage you to implement common standards like JWT.   

By the way, RocketMix contains a security part to handle accesses to catalog parts and administration dashboard. To do that, Management server, Routing server or either "All in One" server automatically generate a users.properties file on startup. This file is reloaded every minutes. Thus, you can edit it and declare your users by adding a line per user like this :

user=password,ROLE_service,enabled

ROLE_service must set to ROLE_ADMIN to grant access to administration dashboard or ROLE_YOURAPI to grant access to API catalog part. Let's take an example :

admin=admin,ROLE_ADMIN,enabled
alex=asuperpassword,ROLE_ADMIN,enabled
tom=bigpassword,ROLE_SUPERAPIPROJECT,enabled
sam=bigbigpassword,ROLE_ADMIN,ROLE_SUPERAPIPROJECT,enabled





