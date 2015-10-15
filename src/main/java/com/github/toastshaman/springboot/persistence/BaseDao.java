package com.github.toastshaman.springboot.persistence;

import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.collect.Maps.newHashMap;

public class BaseDao {

    protected <K, V> Optional<Map<K, V>> getOneElementMaybe(Supplier<Map<K, V>> function) {
        try {
            return Optional.ofNullable(function.get());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected static Map<String, Object> transformKeys(Map<String, Object> map) {
        final HashMap<String, Object> copy = newHashMap();
        map.forEach((key, val) -> copy.put(UPPER_UNDERSCORE.to(LOWER_CAMEL, key.toLowerCase()), val));
        return copy;
    }
}
