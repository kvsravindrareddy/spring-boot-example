package com.lv.springboot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.google.common.base.Strings.isNullOrEmpty;

public class UnirestWrapper {

    public static void setHealthCheckTimeout(Long milliseconds) {
        Unirest.setTimeouts(milliseconds, milliseconds);
    }

    public static void restoreDefaultTimeouts() {
        Unirest.setTimeouts(10000, 60000);
    }

    public static void configure() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
                .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new Jdk8Module());

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    Preconditions.checkArgument(!isNullOrEmpty(value));
                    return jacksonObjectMapper.readValue(value, valueType);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    Preconditions.checkNotNull(value);
                    return jacksonObjectMapper.writeValueAsString(value);
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /*
     * Wraps the Future based Unirest API into a CompletableFuture.
     */
    public static <T> CompletableFuture<HttpResponse<T>> callAsync(Class<T> type, Supplier<HttpRequest> requestSupplier) {
        final CompletableFuture<HttpResponse<T>> responseData = new CompletableFuture<>();
        requestSupplier.get()
            .asObjectAsync(type, new Callback<T>() {
                @Override
                public void completed(HttpResponse<T> httpResponse) {
                    responseData.complete(httpResponse);
                }

                @Override
                public void failed(UnirestException e) {
                    responseData.completeExceptionally(e);
                }

                @Override
                public void cancelled() {
                    responseData.cancel(true);
                }
            });
        return responseData;
    }
}
