package com.example.extractor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Calls a REST endpoint and parses the JSON array response into Post objects. */
public class ApiClient {

    private static final int MAX_ATTEMPTS = 3;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Post> fetchPosts(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();

        IOException lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                HttpResponse<String> response =
                        http.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
                }
                lastError = new IOException("HTTP " + response.statusCode() + " from " + url);
            } catch (IOException e) {
                lastError = e;
            }

            if (attempt < MAX_ATTEMPTS) {
                long waitMs = 1000L * (1L << (attempt - 1)); // 1s, 2s, ...
                System.out.printf("Attempt %d failed (%s). Retrying in %d ms...%n",
                        attempt, lastError.getMessage(), waitMs);
                Thread.sleep(waitMs);
            }
        }
        throw new IOException("All " + MAX_ATTEMPTS + " attempts failed", lastError);
    }
}
