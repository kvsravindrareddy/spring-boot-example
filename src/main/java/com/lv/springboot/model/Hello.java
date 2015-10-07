package com.lv.springboot.model;

import java.util.Optional;

public class Hello {

    private final String name;

    public Hello(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getMiddlename() {
        return Optional.of("Hello");
    }
}
