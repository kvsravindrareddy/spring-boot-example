package com.lv.springboot.resources;

import com.lv.springboot.service.MyApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("users")
public class UserResource {

    private final MyApplication app;

    @Autowired
    public UserResource(MyApplication app) {
        this.app = app;
    }

    @RequestMapping("{id}")
    public Optional<Map<String, Object>> get(@PathVariable BigDecimal id) {
        return app.userFor(id);
    }

    @RequestMapping(method = POST)
    public Map save(@RequestBody Map user) {
        this.app.saveUser(user);
        return Collections.singletonMap("status", "ok");
    }
}
