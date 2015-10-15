package com.github.toastshaman.springboot.util;

import alexh.weak.Dynamic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.toastshaman.springboot.util.ex.InvalidDataStateException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.hateoas.ResourceSupport;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class ModelObject extends ResourceSupport {

    @JsonIgnore
    public final ImmutableMap data;

    public ModelObject(Map data) {
        this.data = ImmutableMap.copyOf(Maps.filterEntries(data, e -> e.getValue() != null));
    }

    /** Name of this instance used for logging purposes, uses class name by default */
    protected String getTypeName() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return data value cast to type, empty if none found
     * @throws ValidationException if found but is not an instance of the input type
     */
    public final <T> Optional<T> getMaybe(String field, Class<T> type) {
        final Object val = data.get(field);
        if (val == null)
            return Optional.empty();
        if (!type.isInstance(val))
            throw new ValidationException(format("%s#%s has erroneous type, %s!=%s",
                getTypeName(), field, val.getClass().getSimpleName(), type.getSimpleName()));
        return Optional.of((T) val);
    }

    /**
     * @return data value cast to type
     * @throws ValidationException if null, or not an instance of the input type
     */
    public final <T> T get(String field, Class<T> type) {
        return getMaybe(field, type)
            .orElseThrow(() -> new ValidationException(format("Missing %s#%s", getTypeName(), field)));
    }

    /**
     * @return data value cast to Map then converted to a ModelObject, empty if none found
     * @throws ValidationException not a Map instance
     */
    public Optional<ModelObject> innerMapMaybe(String field) {
        return getMaybe(field, Map.class).map(data ->
            new NamedModelObject(data, format("%s#%s", getTypeName(), field)));
    }

    /**
     * @return data value cast to Map then converted to a ModelObject
     * @throws ValidationException if null, or not a Map instance
     */
    public ModelObject innerMap(String field) {
        return innerMapMaybe(field)
            .orElseThrow(() -> new ValidationException(format("Missing %s#%s", getTypeName(), field)));
    }

    public static class ValidationException extends InvalidDataStateException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public Dynamic dynamic() {
        return Dynamic.from(data);
    }

    private static class NamedModelObject extends ModelObject {
        private final String name;

        public NamedModelObject(Map data, String name) {
            super(data);
            this.name = name;
        }

        @Override
        protected String getTypeName() {
            return name;
        }
    }
}
