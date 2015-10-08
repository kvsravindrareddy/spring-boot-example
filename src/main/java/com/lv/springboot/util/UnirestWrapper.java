package com.lv.springboot.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UnirestWrapper {

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
