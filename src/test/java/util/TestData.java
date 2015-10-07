package util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class TestData {

    private TestData() {}

    public static Map normalUserKevin() {
        return ImmutableMap.of("firstname", "Kevin", "lastname", "Denver");
    }

    public static Map normalUserPaul() {
        return ImmutableMap.of("firstname", "Paul", "lastname", "Denver");
    }
}
