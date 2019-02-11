# What is RocketMix.github.io ?

\>\>\> [Go to the website](https://rocketmix.github.io "https://rocketmix.github.io") (in french only) <<<

RocketMiX is a free API plateform under Apache Licence 2.0 which helps you to publish your APIs. As many products are complex to understand and complex to deploy, we made RocketMix to be as simple as possible. Just a few Linux executables, nothing more! 

Behind the hood, it's complex and you will not see it. If you want to know more about it, we can tell you that this is a bundled version of Spring Boot API components such as Zuul and Eureka. It also embeds the latests Swagger UI (compliant with OpenAPI specs). It uses Apache CXF (which is a major Jax-RS implementation) to develop API. All these bricks help you to develop your self documented APIs.

# Why should I use RocketMix.github.io ?

Because it is made to be very very (VERY!) simple. It offers you the best result for the less efforts, without any compromises on architecture components. With RocketMix, you have a reverse proxy, a load balancer, a services directory, a portal, an gui to test your APIs, a monitoring dashboard, etc... All these components are embeded into RocketMix, self registered, self monitored.

# I'm excited! How should I start ?

You just need a Linux machine with OpenJDK (>= 8) installed. Download the three executables and launch them. 
* One is for the routing started on http port 8080.
* One is for the managerment server. It runs on port 8761 but its web interface is accessible on port 8080 from the routing server.
* The third one is just an Hello World API demo.

# Now that I want to develop my own API, what should I do ?

Even if the platform is compatible with other languages, this section is written for Java developers. So, you need a developement machine with OpenJDK (>= 8) installed and Maven 3. A good starting point is to read the code of the demo on this repository. To simplify development, we made a Spring Boot Starter kit you can import to your maven project with the following lines :

```XML
<!-- https://mvnrepository.com/artifact/io.github.rocketmix/rocketmix-spring-boot-starter -->
<dependency>
    <groupId>io.github.rocketmix</groupId>
    <artifactId>rocketmix-spring-boot-starter</artifactId>
    <version>1.0.15</version>
</dependency>
```
You project will be able to run without the routing server and management server. It will be accessible localy. Then, if you precise in an the application.yml a management server url, it will be self registered on it and will be reachable through the routing server (on port 8080).



