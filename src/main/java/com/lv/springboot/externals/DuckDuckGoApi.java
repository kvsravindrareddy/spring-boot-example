package com.lv.springboot.externals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import java.util.Map;

import static alexh.Unchecker.uncheckedGet;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Service
public class DuckDuckGoApi {

    private static final Logger log = LoggerFactory.getLogger(DuckDuckGoApi.class);

    @Autowired
    @Value("${mashapeKey}")
    private String mashapeKey;

    @Autowired
    @Value("${duckduckgo.url}")
    private String url;

    @Autowired
    @Qualifier("jerseyClient")
    private Client client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("duckduckgoCache")
    private Cache<String, Map> cache;

    // https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-javanica

    @HystrixCommand(
        fallbackMethod = "fallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
        })
    public Map zeroClickInfo(String q) {
        try {
            return cache.get(q, () -> mapper.readValue(client.target(format(url, q))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .accept("application/x-javascript")
                .header("X-Mashape-Key", mashapeKey)
                .get(String.class), Map.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Map fallback(String q) {
        return uncheckedGet(() -> cache.getIfPresent(q));
    }
}
