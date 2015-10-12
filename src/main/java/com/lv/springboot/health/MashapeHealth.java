package com.lv.springboot.health;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import static com.lv.springboot.util.UnirestWrapper.restoreDefaultTimeouts;
import static com.lv.springboot.util.UnirestWrapper.setHealthCheckTimeout;
import static java.lang.String.format;

@Component
public class MashapeHealth implements HealthIndicator {

    @Autowired @Value("${mashapeKey}")
    private String mashapeKey;

    @Autowired @Value("${duckduckgo.url}")
    private String url;

    @Override
    public Health health() {
        try {
            setHealthCheckTimeout(500L);
            final HttpResponse<String> response = Unirest.options(format(url, "London")).header("X-Mashape-Key", mashapeKey).asString();
            return response.getStatus() == 405 ? Health.up().build() : Health.down().withDetail("status", response.getStatus()).build();
        }
        catch (Exception e) {
            return Health.down(e).build();
        }
        finally {
            restoreDefaultTimeouts();
        }
    }
}
