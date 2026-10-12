package com.dgca.flightlogbook.ui.api;

import com.dgca.flightlogbook.ui.Session;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static String baseUrl = DEFAULT_BASE_URL;

    public static void setBaseUrl(String url) {
        baseUrl = url;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static <T> T get(String endpoint, Class<T> responseType) throws ApiException {
        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET()
                    .header("Accept", "application/json");

            addAuthHeader(reqBuilder);

            HttpResponse<String> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
            handleErrorIfNeeded(response);

            if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseType);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Network error: " + e.getMessage(), e);
        }
    }

    public static <T> T get(String endpoint, TypeReference<T> responseType) throws ApiException {
        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET()
                    .header("Accept", "application/json");

            addAuthHeader(reqBuilder);

            HttpResponse<String> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
            handleErrorIfNeeded(response);

            if (response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseType);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Network error: " + e.getMessage(), e);
        }
    }

    public static <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws ApiException {
        try {
            String jsonBody = requestBody != null ? mapper.writeValueAsString(requestBody) : "";
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            addAuthHeader(reqBuilder);

            HttpResponse<String> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
            handleErrorIfNeeded(response);

            if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseType);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Network error: " + e.getMessage(), e);
        }
    }

    public static <T> T put(String endpoint, Object requestBody, Class<T> responseType) throws ApiException {
        try {
            String jsonBody = requestBody != null ? mapper.writeValueAsString(requestBody) : "";
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            addAuthHeader(reqBuilder);

            HttpResponse<String> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
            handleErrorIfNeeded(response);

            if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseType);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Network error: " + e.getMessage(), e);
        }
    }

    private static void addAuthHeader(HttpRequest.Builder reqBuilder) {
        String token = Session.getToken();
        if (token != null && !token.isBlank()) {
            reqBuilder.header("Authorization", "Bearer " + token);
        }
    }

    private static void handleErrorIfNeeded(HttpResponse<String> response) throws ApiException {
        int status = response.statusCode();
        if (status >= 400) {
            String errorMessage = parseErrorMessage(response.body(), status);
            throw new ApiException(status, errorMessage);
        }
    }

    private static String parseErrorMessage(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.isBlank()) {
            return "Server responded with status code " + statusCode;
        }
        try {
            JsonNode root = mapper.readTree(responseBody);
            if (root.has("message") && !root.get("message").asText().isBlank()) {
                return root.get("message").asText();
            }
            if (root.has("error") && !root.get("error").asText().isBlank()) {
                return root.get("error").asText();
            }
        } catch (IOException ignored) {
        }
        return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
    }
}
