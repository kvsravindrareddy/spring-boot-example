package com.github.toastshaman.springboot.util;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Utility container for general use translation / validation functions
 */
public class MapTransformFunctions {

    public static final Pattern DECIMAL = Pattern.compile("[\\+\\-]?[0-9]+\\.?[0-9]*");
    public static final Pattern UNSIGNED_DECIMAL = Pattern.compile("[0-9]+\\.?[0-9]*");

    public static <T> Function<T, T> check(Predicate<T> predicate, String failMessage) {
        return t -> {
            checkArgument(predicate.test(t), failMessage);
            return t;
        };
    }

    public static <T> Function<Object, T> checkClass(Class<T> clazz) {
        return val -> {
            checkArgument(clazz.isAssignableFrom(val.getClass()),
                "must be type " + clazz.getSimpleName() + " [found " + val.getClass().getSimpleName() + "]");
            return (T) val;
        };
    }

    public static <T> Function<Object, T> checkIsOneOf(Collection<? extends T> validValues) {
        return val -> {
            checkArgument(validValues.contains(val),
                "must be one of " + validValues);
            return (T) val;
        };
    }

    @SafeVarargs
    public static <T> Function<Object, T> checkIsOneOf(T arg1, T... args) {
        return checkIsOneOf(ImmutableList.<T>builder()
            .add(arg1)
            .addAll(asList(args))
            .build());
    }

    private static String invalidEnumNameCharacters = "[^0-9A-Za-z_$]+";
    private static String startEndInvalidEnumNameCharacter = format("^%s|%<s$", invalidEnumNameCharacters);

    private static Enum<?> checkMatchesAnyEnum(String s, Collection<Enum<?>> enums) {
        final String name = s.replaceAll(startEndInvalidEnumNameCharacter, "").replaceAll(invalidEnumNameCharacters, "_").toUpperCase();
        return enums.stream()
            .filter(enumeration -> name.equals(enumeration.name()) || s.equals(enumeration.toString()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(format("'%s' (translated to '%s') doesn't match any of %s",
                s, name, enums)));
    }

    /**
     * Matches a string against enumeration values, will try to match to #name() or #toString(), #name() match will remove
     * non-enum name characters from input string not consider case
     *
     * @param enumClass enumeration class instances of which will be matched
     * @return matched enum#name()
     * @throws IllegalArgumentException no match
     */
    public static <E extends Enum<E>> Function<Object, E> checkMatches(Class<E> enumClass) {
        return checkClass(String.class).andThen(s -> (E) checkMatchesAnyEnum(s, asList(enumClass.getEnumConstants())));
    }

    /**
     * Matches a string against multiple enumeration class values
     *
     * @see MapTransformFunctions#checkMatches(Class)
     */
    @SafeVarargs
    public static Function<Object, Enum<?>> checkMatchesAnyOf(Class<? extends Enum<?>> enumClass,
                                                              Class<? extends Enum<?>>... others) {
        List<Enum<?>> enums = Stream.concat(asList(enumClass.getEnumConstants()).stream(),
            Stream.of(others).flatMap(clazz -> asList(clazz.getEnumConstants()).stream()))
            .collect(toList());

        return checkClass(String.class).andThen(s -> checkMatchesAnyEnum(s, enums));
    }

    public static Function<Object, String> checkToString(Pattern regex) {
        return val -> {
            final String stringRepresentation = val.toString();
            checkArgument(regex.matcher(stringRepresentation).matches(), "must match " + regex);
            return stringRepresentation;
        };
    }

    public static Function<Object, String> checkToString(String regex) {
        return checkToString(Pattern.compile(regex));
    }

    public static Function<String, String> maxLength(int length) {
        return val -> {
            checkArgument(val.length() <= length, "must be at most " + length + " characters");
            return val;
        };
    }

    public static Function<Object, Map> transformWith(MapTransformer transformer) {
        return new SingleTransform(transformer);
    }

    public static Function<Object, List<Map>> transformCollectionWith(MapTransformer transformer) {
        return new CollectionTransform(transformer);
    }

    private MapTransformFunctions() {/* static */}

    /**
     * An object that contains a MapTransformer within
     */
    public static abstract class InnerTransform {
        private final MapTransformer transformer;

        public InnerTransform(MapTransformer transformer) {
            this.transformer = transformer;
        }

        public MapTransformer transformer() {
            return transformer;
        }

        public abstract Object filterExternal(Object o);

        public abstract Object mapStrict(Object o);
    }

    public static class SingleTransform extends InnerTransform implements Function<Object, Map> {
        public SingleTransform(MapTransformer transformer) {
            super(transformer);
        }

        @Override
        public Map apply(Object o) {
            return checkClass(Map.class).andThen(transformer()::map).apply(o);
        }

        @Override
        public Object filterExternal(Object o) {
            return checkClass(Map.class)
                .andThen(m -> transformer().filterExternal(m))
                .apply(o);
        }

        @Override
        public Object mapStrict(Object o) {
            if (o == null) return null;
            return checkClass(Map.class).andThen(transformer()::mapStrict).apply(o);
        }
    }

    public static class CollectionTransform extends InnerTransform implements Function<Object, List<Map>> {
        public CollectionTransform(MapTransformer transformer) {
            super(transformer);
        }

        @Override
        public List<Map> apply(Object o) {
            return checkClass(Collection.class).andThen(collection ->
                ((Collection<?>) collection).stream()
                    .map(transformWith(transformer()))
                    .collect(toList())).apply(o);
        }

        @Override
        public Object filterExternal(Object o) {
            return checkClass(Collection.class)
                .andThen(c -> ((Stream<?>) c.stream())
                    .filter(Map.class::isInstance)
                    .map(m -> transformer().filterExternal((Map) m))
                    .collect(toList()))
                .apply(o);
        }

        @Override
        public Object mapStrict(Object o) {
            if (o == null) return null;
            return checkClass(Collection.class)
                .andThen(c -> ((Stream<?>) c.stream())
                    .map(checkClass(Map.class))
                    .map(transformer()::mapStrict)
                    .collect(toList()))
                .apply(o);
        }
    }
}