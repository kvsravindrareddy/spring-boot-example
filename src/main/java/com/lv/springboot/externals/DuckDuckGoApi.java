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

import static com.google.common.collect.Maps.newHashMap;
import static com.lv.springboot.util.UnirestWrapper.unirest;
import static java.lang.String.format;

@Service
public class DuckDuckGoApi {

    private static final Logger log = LoggerFactory.getLogger(DuckDuckGoApi.class);

    private final String mashapeKey;
    private final String url;

    @Autowired
    public DuckDuckGoApi(@Value("${mashapeKey}") String mashapeKey,
                         @Value("${duckduckgo.url}") String url) {
        this.mashapeKey = mashapeKey;
        this.url = url;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public Map zeroClickInfo(String q) {
        final HttpResponse<Map> response = unirest(() -> Unirest.get(format(url, q))
            .header("X-Mashape-Key", mashapeKey)
            .header("Accept", "application/json"))
            .join();

        if (response.getStatus() == 200) return response.getBody();
        throw new DuckDuckGoException(response.getStatusText());
    }

    public Map fallback(String q) {
        return newHashMap();
    }
}
