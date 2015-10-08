package com.lv.springboot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UnirestWrapper {

    public static void configure() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

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
