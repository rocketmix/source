# What is RocketMix.github.io ?

RocketMiX is a free API plateform under Apache Licence 2.0 which helps you to publish your APIs. As many products are complex to understand and complex to deploy, we made RocketMix to be as simple as possible. Just a few Linux executables, nothing more! 

Behind the hood, it's complex and you will not see it. If you want to know more about it, we can tell you that this is a bundled version of Spring Boot API components such as Zuul and Eureka. It also embeds the latests Swagger UI (compliant with OpenAPI specs). It uses Apache CXF (which is a major Jax-RS implementation) to develop API. All these bricks help you to develop your self documented APIs.

# Why should I use RocketMix.github.io ?

Because it is made to be very very (VERY!) simple. It offers you the best result for the less efforts, without any compromises on architecture components. With RocketMix, you have a reverse proxy, a load balancer, a services directory, a portal, an gui to test your APIs, a monitoring dashboard, etc... All these components are embeded into RocketMix, self registered, self monitored.

# I'm excited! How should I start ?

You just need a Linux machine with OpenJDK (>= 8) installed. Download the three executables and launch them. One is for the routing started on http port 8080. One is for the managerment server (which starts on port 8761 but you don't have to care about it because its web interface is accessible on port 8080 from the routing server. The third one is just an Hello World API demo.

# Now that I want to develop my own API, what should I do ?

You will need a developement machine with OpenJDK (>= 8) installed and Maven 3. Clone this repo and compile everything to check if all is OK (mvn clean compile package). Then, you will see that there's a Spring boot started project. Just create your Maven project based on it and follow the sample codes. You project will be able to run without the routing server and management server. It will be accessible localy. Then, if you precise a the application.yml a management server url, it will self register on it and will be reachable through the routing server (port 8080).    
