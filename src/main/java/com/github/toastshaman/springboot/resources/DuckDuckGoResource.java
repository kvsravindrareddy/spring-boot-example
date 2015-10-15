package com.github.toastshaman.springboot.resources;

import com.codahale.metrics.annotation.Timed;
import com.github.toastshaman.springboot.externals.DuckDuckGoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("duckduckgo")
public class DuckDuckGoResource {

    @Autowired
    private DuckDuckGoApi api;

    @Timed
    @RequestMapping
    public Map query(@RequestParam String q) {
        return api.zeroClickInfo(q);
    }
}
