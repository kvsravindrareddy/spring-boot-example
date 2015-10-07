package com.lv.springboot.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FailingHealth implements HealthIndicator {

    @Override
    public Health health() {
        final int errorCode = check();
        if (errorCode != 0) return Health.down().withDetail("Error Code", errorCode).build();
        return Health.up().build();
    }

    public int check() {
        return 1;
    }
}
