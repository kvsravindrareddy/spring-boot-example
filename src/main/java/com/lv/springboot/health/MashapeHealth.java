package com.lv.springboot.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
public class MashapeHealth implements HealthIndicator {

    @Autowired @Value("${mashapeKey}")
    private String mashapeKey;

    @Autowired @Value("${duckduckgo.url}")
    private String url;

    @Autowired @Qualifier("healthCheckJerseyClient")
    private Client client;

    @Override
    public Health health() {
        final Response response = client.target(format(url, "London"))
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .accept("application/x-javascript")
            .header("X-Mashape-Key", mashapeKey)
            .options();

        return response.getStatus() == 405 ? Health.up().build() : Health.down()
            .withDetail("status", response.getStatus())
            .withDetail("msg", "Failed doing OPTIONS: " + format(url, "London"))
            .build();
    }
}
