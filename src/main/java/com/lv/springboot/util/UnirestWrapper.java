package com.lv.springboot.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UnirestWrapper {

    public static CompletableFuture<HttpResponse<Map>> unirest(Supplier<HttpRequest> requestSupplier) {
        final CompletableFuture<HttpResponse<Map>> responseData = new CompletableFuture<>();
        requestSupplier.get()
            .asObjectAsync(Map.class, new Callback<Map>() {
                @Override
                public void completed(HttpResponse<Map> httpResponse) {
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
