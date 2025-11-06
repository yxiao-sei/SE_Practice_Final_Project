package com.cloume.ecnu.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author yxiao
 */
@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories("com.cloume.ecnu.*")
@EntityScan("com.cloume.ecnu.plms.*")
@ComponentScan("com.cloume.ecnu.*")
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}
