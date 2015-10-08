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
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.boot.autoconfigure.security.SecurityProperties.ACCESS_OVERRIDE_ORDER;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@SpringBootApplication
@ComponentScan
@Configuration

@EnableAutoConfiguration
@EnableHystrix
@EnableHystrixDashboard
@EnableHypermediaSupport(type = HAL)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class Application {

    public static void main(String[] args) {
        UnirestWrapper.configure();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jdk8Module jacksonJdkModule() {
        return new Jdk8Module();
    }

    @Configuration
    @Order(ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
        }
    }
}
