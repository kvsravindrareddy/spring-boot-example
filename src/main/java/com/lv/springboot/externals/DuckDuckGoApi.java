package com.lv.springboot.externals;

import com.lv.springboot.externals.ex.DuckDuckGoException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static alexh.Unchecker.uncheckedGet;
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
        return uncheckedGet(() -> {
            final HttpResponse<Map> response = Unirest.get(format("https://duckduckgo-duckduckgo-zero-click-info.p.mashape.com/?format=json&no_html=1&no_redirect=1&q=%s&skip_disambig=1", q))
                    .header("X-Mashape-Key", mashapeKey)
                    .header("Accept", "application/json")
                    .asObject(Map.class);

            if (response.getStatus() == 200) return response.getBody();
            log.error("Request to DuckDuckGo failed with status code " + response.getStatus());
            throw new DuckDuckGoException(response.getStatusText());
        });
    }

    public Map fallback(String q) {
        return newHashMap();
    }
}
