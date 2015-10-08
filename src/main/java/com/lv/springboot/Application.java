package com.lv.springboot;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.lv.springboot.util.UnirestWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@SpringBootApplication
@ComponentScan
@Configuration

@EnableAutoConfiguration
@EnableHystrix
@EnableHystrixDashboard
@EnableHypermediaSupport(type = HAL)
public class Application {

    public static void main(String[] args) {
        UnirestWrapper.configure();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jdk8Module jacksonJdkModule() {
        return new Jdk8Module();
    }
}
