package com.cloume.ecnu.serviceregistrycenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryCenterApplication.class, args);
    }

}
