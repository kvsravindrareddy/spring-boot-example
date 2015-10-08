package com.lv.springboot.externals;

import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

@Service
public class DuckDuckGoApi {

    private static final Logger log = LoggerFactory.getLogger(DuckDuckGoApi.class);

    private final String mashapeKey;

    @Autowired
    public DuckDuckGoApi(@Value("${mashapeKey}") String mashapeKey) {
        this.mashapeKey = mashapeKey;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public Map zeroClickInfo(String q) {
        try {
            return Unirest.get(format("https://duckduckgo-duckduckgo-zero-click-info.p.mashape.com/?format=json&no_html=1&no_redirect=1&q=%s&skip_disambig=1", q))
                    .header("X-Mashape-Key", mashapeKey)
                    .header("Accept", "application/json")
                    .asObject(Map.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Map fallback(String q) {
        return newHashMap();
    }
}
