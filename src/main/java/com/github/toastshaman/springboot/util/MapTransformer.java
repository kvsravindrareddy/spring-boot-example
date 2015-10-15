package com.github.toastshaman.springboot.util;

import com.github.toastshaman.springboot.util.ex.InvalidFieldException;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.github.toastshaman.springboot.util.EntryCollectors.toMap;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

public abstract class MapTransformer {

    public static MapTransformer of(Map<Field, Function<Map, ?>> transforms) {
        return new MapTransformer() {
            @Override
            public Map<Field, Function<Map, ?>> transforms() {
                return transforms;
            }
        };
    }

    public static MapTransformer of(Stream<Map.Entry<Field, Function<Map, ?>>> transforms) {
        return of(transforms.collect(toMap()));
    }

    @SafeVarargs
    public static MapTransformer of(Map.Entry<Field, Function<Map, ?>>... transforms) {
        return of(Stream.of(transforms));
    }

    /** Map of transform fields -> transformer functions */
    public abstract Map<Field, Function<Map, ?>> transforms();

    /**
     * Returns a new map (as supplied) with known & valid schema values from the input map
     * @param data field value map
     * @param mapSupplier supplier of map to feed into
     * @return new db object
     * @throws InvalidFieldException for invalid field values, ie the mapper function has thrown
     */
    public <T extends Map> T map(final Map data, final Supplier<T> mapSupplier) {
        T result = mapSupplier.get();
        transforms().forEach((field, mapper) -> {
            Objects.requireNonNull(field, "null field, check transforms() implementation");
            Objects.requireNonNull(mapper, "null mapper, check transforms() implementation");

            final Object val;
            try { val = mapper.apply(data); }
            catch (RuntimeException e) {
                throw new InvalidFieldException(this.getClass(), field.name() + " = " + truncatedStringValue(data.get(field.name()), 50), e);
            }
            if (val != null)
                result.put(field.keyTransform(), val);
        });
        return result;
    }

    private String truncatedStringValue(Object val, int maxLength) {
        String valString = String.valueOf(val);
        if (valString.length() > maxLength)
            return valString.substring(0, maxLength-3) + "...";
        return valString;
    }

    /**
     * As {@link #map(Map, Supplier)} but throws validation exception on any unknown data field
     * Recursively calls nested MapTransformer mapStrict methods
     * @throws InvalidFieldException unknown field values
     */
    public final <T extends Map> T mapStrict(Map data, Supplier<T> mapSupplier) {
        Set unknownKeys = Sets.difference(data.keySet(), beforeKeys());
        if (!unknownKeys.isEmpty())
            throw new InvalidFieldException("Unknown fields in transform: " + unknownKeys);

        transforms().forEach((field, fn) -> {
            if (fn instanceof MapTransformEntries.InnerAwareMapFunction) {
                try {
                    ((MapTransformEntries.InnerAwareMapFunction) fn).innerTransform()
                        .ifPresent(transform -> transform.mapStrict(data.get(field.name())));
                }
                catch (InvalidFieldException e) {
                    throw new InvalidFieldException(format("\"%s\": %s", field.name(), e.getMessage()));
                }
            }
        });

        return map(data, mapSupplier);
    }

    /**
     * Shortcut for @see Schema#map(Map, Supplier) using default map implementation
     * Subclasses should override {@link #map(Map, Supplier)}
     */
    public final Map map(Map data) {
        return map(data, HashMap::new);
    }

    /** As {@link #map(Map)} but throws validation exception on any unknown data field */
    public final Map mapStrict(Map data) {
        return mapStrict(data, HashMap::new);
    }

    /** Subclasses should override {@link #map(Map, Supplier)} */
    public final ModelObject mapToModelObject(Map data) {
        return new ModelObject(map(data));
    }

    /** Subclasses should override {@link #map(Map, Supplier)} */
    public final ModelObject map(ModelObject mObject) {
        return mapToModelObject(mObject.data);
    }

    /** @return string field names for this schema's transforms */
    public Set<String> beforeKeys() {
        return transforms().keySet().stream().map(Field::name).collect(toSet());
    }

    /** @return string field names for this schema's transforms */
    public Set<String> afterKeys() {
        return transforms().keySet().stream().map(Field::keyTransform).collect(toSet());
    }

    /**
     * Mutates input map updating known schema fields with values contained in update map
     * @param data object to mutate
     * @param update map containing updated values
     * @return modified input dbObject
     * @throws InvalidFieldException for invalid update values, ie the mapper function has thrown
     */
    public <T extends Map> T update(T data, Map update) {
        transforms().forEach((field, mapper) -> {
            try {
                if (update.containsKey(field.name()))
                    data.put(field.keyTransform(), mapper.apply(update));
            }
            catch (RuntimeException e) {
                throw new InvalidFieldException(this.getClass(), field.name() + " = " + update.get(field.name()), e);
            }
        });
        return data;
    }

    public boolean isNonInnerField(String beforeName) {
        return toField(beforeName)
            .filter(f -> !f.isInner())
            .isPresent();
    }

    private Optional<Field> toField(String beforeName) {
        return toFieldEntry(beforeName)
            .map(Map.Entry::getKey);
    }

    private Optional<Map.Entry<Field, Function<Map, ?>>> toFieldEntry(String beforeName) {
        return transforms().entrySet()
            .stream()
            .filter(e -> e.getKey().name().equals(beforeName))
            .findFirst();
    }

    /**
     * @return individual map -> Object transform for the matching field
     * @throws IllegalArgumentException
     */
    public Function<Map, ?> transformField(String beforeName) {
        return transforms().entrySet()
            .stream()
            .filter(entry -> entry.getKey().name().equals(beforeName))
            .findAny()
            .map(Map.Entry::getValue)
            .orElseThrow(() -> new IllegalArgumentException(beforeName + " not found"));
    }

    /**
     * Recursively calls nested MapTransformer filterExternal methods
     * @return new map with non-inner schema keys
     */
    public <K, V> Map<String, V> filterExternal(Map<K, V> incoming) {
        final Map<String, V> map = new HashMap<>();
        incoming.entrySet().stream()
            .filter(e -> e.getKey() instanceof String)
            .map(e -> (Map.Entry<String, V>) e)
            .filter(e -> isNonInnerField(e.getKey()))
            .forEach(e -> {
                // if field maps to a nested transform use recursive filtering
                final Object value = toFieldEntry(e.getKey())
                    .filter(entry -> e.getValue() != null) // null value may be valid, but can't filter it
                    .map(Map.Entry::getValue)
                    .filter(MapTransformEntries.InnerAwareMapFunction.class::isInstance)
                    .map(MapTransformEntries.InnerAwareMapFunction.class::cast)
                    .flatMap(f -> f.innerTransform())
                    .map(innerTransform -> innerTransform.filterExternal(e.getValue()))

                    .orElseGet(e::getValue);

                map.put(e.getKey(), (V) value);
            });
        return map;
    }

    public static class Field {
        protected final String fieldName;
        protected final String keyTransform;
        protected final boolean inner;

        protected Field(String fieldName, String keyTransform, boolean inner) {
            this.fieldName = fieldName;
            this.keyTransform = keyTransform;
            this.inner = inner;
        }

        public Field(String fieldName) {
            this.fieldName = fieldName;
            this.keyTransform = fieldName;
            this.inner = false;
        }

        public String name() {
            return fieldName;
        }

        public boolean isInner() {
            return inner;
        }

        public String keyTransform() {
            return keyTransform;
        }

        public Field withKeyTransform(String keyTransform) {
            return new Field(fieldName, keyTransform, inner);
        }

        public Field withInner(boolean inner) {
            return new Field(fieldName, keyTransform, inner);
        }

        @Override
        public String toString() {
            return toStringHelper(Field.class)
                .add("name", fieldName)
                .toString();
        }
    }
}
