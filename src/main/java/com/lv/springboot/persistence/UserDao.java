package com.lv.springboot.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class UserDao extends BaseDao {

    @Autowired
    private JdbcTemplate template;

    public Optional<Map<String, Object>> userFor(BigDecimal id) {
        return getOneElementMaybe(() -> template.queryForMap("select * from user where id = ?", id))
                .map(BaseDao::transformKeys);
    }

    public int insert(Map user) {
        return template.update("insert into user (firstname, lastname) values (?, ?)", user.get("firstname"), user.get("lastname"));
    }
}
