package com.lv.springboot.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealth implements HealthIndicator {

    @Autowired
    private JdbcTemplate template;

    @Override
    public Health health() {
        try {
            template.queryForObject("select 1 from dual", Long.class);
            return Health.up().build();
        }
        catch (Exception e) { return Health.down(e).build(); }
    }
}
