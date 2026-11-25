package com.example.extractor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One record returned by the API. Unknown JSON fields are ignored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Post(int userId, int id, String title, String body) {
}
