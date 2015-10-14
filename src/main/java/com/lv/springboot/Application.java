package com.lv.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.ws.rs.client.Client;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
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

    // Start with -Dspring.profiles.active="local"
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean @Primary
    public ObjectMapper jacksonBuilder() {
        return new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new Jackson2HalModule())
            .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
            .configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean
    public Client jerseyClient() {
        return JerseyClientBuilder.createClient().register(new LoggingFilter());
    }

    @Bean(name = "healthCheckJerseyClient")
    public Client healthCheckJerseyClient() {
        return JerseyClientBuilder.createClient(new ClientConfig()
            .property(READ_TIMEOUT, 500)
            .property(CONNECT_TIMEOUT, 500))
            .register(new LoggingFilter());
    }

    @Configuration
    @Order(ACCESS_OVERRIDE_ORDER)
    @SuppressWarnings("unused")
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            // configure an authentication manager
        }
    }
}
