package com.lv.springboot.resources;

import com.lv.springboot.model.User;
import com.lv.springboot.service.MyApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("users")
public class UserResource {

    @Autowired
    private MyApplication app;

    @RequestMapping("{id}")
    public Optional<User> get(@PathVariable BigDecimal id) {
        return app.userFor(id).map(User::new);
    }

    @RequestMapping(method = POST)
    public Map save(@RequestBody Map user) {
        this.app.saveUser(user);
        return singletonMap("status", "ok");
    }
}
