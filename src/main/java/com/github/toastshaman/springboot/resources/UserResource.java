package com.github.toastshaman.springboot.resources;

import com.codahale.metrics.annotation.Timed;
import com.github.toastshaman.springboot.model.User;
import com.github.toastshaman.springboot.model.UserSchema;
import com.github.toastshaman.springboot.service.MyApplication;
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

    @Timed
    @RequestMapping("{id}")
    public Optional<User> get(@PathVariable BigDecimal id) {
        return app.userFor(id).map(User::new);
    }

    @Timed
    @RequestMapping(method = POST)
    public Map save(@RequestBody Map user) {
        this.app.saveUser(UserSchema.INSTANCE.map(user));
        return singletonMap("status", "ok");
    }
}
