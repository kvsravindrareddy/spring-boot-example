package com.github.toastshaman.springboot.util;

import com.github.toastshaman.springboot.util.ex.FieldRequiredException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Util class to aid in generating MapTransformer.Field -> Function<Map, ?> entries */
public class MapTransformEntries {

    public static Entry nonNull(String field, Function<Object, ?> mapper) {
        return new NonNullEntry(new MapTransformer.Field(field), mapper);
    }

    public static Entry nonNull(String field, String toKey, Function<Object, ?> mapper) {
        return new NonNullEntry(new MapTransformer.Field(field).withKeyTransform(toKey), mapper);
    }

    /** Mostly the same as @see SchemaUtil#nonNull, but marks the field as 'inner' which we should ignore external input to */
    public static Entry innerNonNull(String field, Function<Object, ?> mapper) {
        return new NonNullEntry(new MapTransformer.Field(field).withInner(true), mapper);
    }

    public static Entry innerNonNull(String field, String toKey, Function<Object, ?> mapper) {
        return new NonNullEntry(new MapTransformer.Field(field).withKeyTransform(toKey).withInner(true), mapper);
    }

    public static Entry nullable(String field, Function<Object, ?> mapper) {
        return new DefaultableEntry(new MapTransformer.Field(field), mapper);
    }

    public static Entry nullable(String field, String toKey, Function<Object, ?> mapper) {
        return new DefaultableEntry(new MapTransformer.Field(field).withKeyTransform(toKey), mapper);
    }

    /** Mostly the same as @see SchemaUtil#nullable, but marks the field as 'inner' which we should ignore external input to */
    public static Entry innerNullable(String field, Function<Object, ?> mapper) {
        return new DefaultableEntry(new MapTransformer.Field(field).withInner(true), mapper);
    }

    public static Entry innerNullable(String field, String toKey, Function<Object, ?> mapper) {
        return new DefaultableEntry(new MapTransformer.Field(field).withKeyTransform(toKey).withInner(true), mapper);
    }

    public static <T, D extends T> Entry defaultable(String field, Function<Object, T> mapper, Function<Map, D> fallback) {
        Objects.requireNonNull(fallback);
        return new DefaultableEntry(new MapTransformer.Field(field), mapper, fallback);
    }

    public static <T, D extends T> Entry defaultable(String field, String toKey, Function<Object, T> mapper, Function<Map, D> fallback) {
        Objects.requireNonNull(fallback);
        return new DefaultableEntry(new MapTransformer.Field(field).withKeyTransform(toKey), mapper, fallback);
    }

    /** Mostly the same as @see SchemaUtil#defaultable, but marks the field as 'inner' which we should ignore external input to */
    public static <T, D extends T> Entry innerDefaultable(String field, Function<Object, T> mapper, Function<Map, D> fallback) {
        Objects.requireNonNull(fallback);
        return new DefaultableEntry(new MapTransformer.Field(field).withInner(true), mapper, fallback);
    }

    /** Mostly the same as @see SchemaUtil#defaultable, but marks the field as 'inner' which we should ignore external input to */
    public static <T, D extends T> Entry innerDefaultable(String field, String toKey, Function<Object, T> mapper, Function<Map, D> fallback) {
        Objects.requireNonNull(fallback);
        return new DefaultableEntry(new MapTransformer.Field(field).withKeyTransform(toKey).withInner(true), mapper, fallback);
    }

    public static Map.Entry<MapTransformer.Field, Function<Map, ?>> customMap(String beforeKey, String afterKey, Function<Map, ?> fullMapper) {
        return new AbstractMap.SimpleImmutableEntry<>(new MapTransformer.Field(beforeKey).withKeyTransform(afterKey), fullMapper);
    }

    public static Map.Entry<MapTransformer.Field, Function<Map, ?>> customMap(String key, Function<Map, ?> fullMapper) {
        return customMap(key, key, fullMapper);
    }

    private MapTransformEntries() {}

    public static class InnerAwareMapFunction implements Function<Map, Object> {

        private final Function<Object, ?> mapper;
        private final Function<Map, ?> fullMapper;

        public InnerAwareMapFunction(Function<Object, ?> mapper, Function<Map, ?> fullMapper) {
            this.mapper = mapper;
            this.fullMapper = fullMapper;
        }

        @Override
        public Object apply(Map map) {
            return fullMapper.apply(map);
        }

        public Function<Object, ?> innerMapper() {
            return mapper;
        }

        public Optional<MapTransformFunctions.InnerTransform> innerTransform() {
            return Optional.of(mapper)
                .filter(MapTransformFunctions.InnerTransform.class::isInstance)
                .map(MapTransformFunctions.InnerTransform.class::cast);
        }
    }

    public static abstract class Entry extends AbstractMap.SimpleImmutableEntry<MapTransformer.Field, Function<Map, ?>> {

        public Entry(MapTransformer.Field key, Function<Object, ?> mapper, Function<Map, ?> fullMapper) {
            super(key, new InnerAwareMapFunction(mapper, fullMapper));
        }
    }

    public static class DefaultableEntry extends Entry {

        public DefaultableEntry(MapTransformer.Field field, Function<Object, ?> mapper, Function<Map, ?> fallback) {
            super(field, mapper, x -> Optional.ofNullable(x.get(field.name()))
                .map(mapper)
                .orElse(fallback.apply(x)));
        }

        /** defaults -> null */
        public DefaultableEntry(MapTransformer.Field field, Function<Object, ?> mapper) {
            this(field, mapper, x -> null);
        }
    }

    public static class NonNullEntry extends Entry {

        public NonNullEntry(MapTransformer.Field field, Function<Object, ?> mapper) {
            super(field, mapper, x -> {
                final Optional<?> mappedValue = Optional.ofNullable(x.get(field.name())).map(mapper);
                if (!mappedValue.isPresent())
                    throw new FieldRequiredException("is required");

                return mappedValue.orElse(null);
            });
        }
    }
}
