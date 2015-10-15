package com.github.toastshaman.springboot.model;

import com.github.toastshaman.springboot.util.MapTransformer;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.toastshaman.springboot.util.EntryCollectors.toMap;
import static com.github.toastshaman.springboot.util.MapTransformEntries.nonNull;
import static com.github.toastshaman.springboot.util.MapTransformEntries.nullable;
import static com.github.toastshaman.springboot.util.MapTransformFunctions.checkClass;

public class UserSchema extends MapTransformer {

    public static final UserSchema INSTANCE = new UserSchema();

    @Override
    public Map<Field, Function<Map, ?>> transforms() {
        return Stream.of(
            nonNull("firstname", checkClass(String.class)),
            nonNull("lastname", checkClass(String.class)),
            nullable("middlename", checkClass(String.class))
        ).collect(toMap());
    }
}
