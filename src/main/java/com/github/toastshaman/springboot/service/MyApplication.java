package com.github.toastshaman.springboot.service;

import com.github.toastshaman.springboot.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class MyApplication {

    @Autowired
    private UserDao userDao;

    @Transactional
    public void saveUser(Map user) {
        userDao.insert(user);
    }

    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> userFor(BigDecimal id) {
        return userDao.userFor(id);
    }
}
