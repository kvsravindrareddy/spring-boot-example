package com.github.toastshaman.springboot.util;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EntryCollectors {

    /** @return collector for Map.Entry instances */
    public static <T extends Map.Entry<? extends K, ? extends U>, K, U> Collector<T, ?, Map<K, U>> toMap() {
        return Collectors.toMap(e -> e.getKey(), e -> e.getValue());
    }

    private EntryCollectors() {}
}
