package com.lv.springboot.externals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import java.util.Map;

import static alexh.Unchecker.uncheckedGet;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;

@Service
public class DuckDuckGoApi {

    @Autowired
    @Value("${mashapeKey}")
    private String mashapeKey;

    @Autowired
    @Value("${duckduckgo.url}")
    private String url;

    @Autowired @Qualifier("jerseyClient")
    private Client client;

    @Autowired
    private ObjectMapper mapper;

    @HystrixCommand(
        fallbackMethod = "fallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
        })
    public Map zeroClickInfo(String q) {
        return uncheckedGet(() -> mapper.readValue(client.target(format(url, q))
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .accept("application/x-javascript")
            .header("X-Mashape-Key", mashapeKey)
            .property(READ_TIMEOUT, 500)
            .get(String.class), Map.class));
    }

    public Map fallback(String q) {
        return newHashMap();
    }
}
