package com.lv.springboot.externals.ex;

public class DuckDuckGoException extends RuntimeException {
    public DuckDuckGoException(String statusText) {
        super(statusText);
    }
}
