package com.lv.springboot.externals;

import com.lv.springboot.externals.ex.DuckDuckGoException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.lv.springboot.util.UnirestWrapper.callAsync;
import static java.lang.String.format;

@Service
public class DuckDuckGoApi {

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
        final HttpResponse<Map> response = callAsync(Map.class, () -> Unirest.get(format(url, q))
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
