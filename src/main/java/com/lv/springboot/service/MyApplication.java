package com.lv.springboot.service;

import com.lv.springboot.persistence.Transactor;
import com.lv.springboot.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class MyApplication {

    private final Transactor transactor;
    private final UserDao userDao;

    @Autowired
    public MyApplication(Transactor transactor, UserDao userDao) {
        this.transactor = transactor;
        this.userDao = userDao;
    }

    public void saveUser(Map user) {
        transactor.run(status -> userDao.insert(user));
    }

    public Optional<Map<String, Object>> userFor(BigDecimal id) {
        return transactor.call(status -> userDao.userFor(id));
    }
}
