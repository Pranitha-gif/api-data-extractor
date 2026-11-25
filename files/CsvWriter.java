package com.example.extractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Writes posts to a CSV file. */
public class CsvWriter {

    public void write(List<Post> posts, Path file) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        try (BufferedWriter out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            out.write("userId,id,title,body");
            out.newLine();
            for (Post p : posts) {
                out.write(p.userId() + "," + p.id() + "," + escape(p.title()) + "," + escape(p.body()));
                out.newLine();
            }
        }
    }

    /** Wraps a value in quotes and doubles any inner quotes (RFC 4180 style). */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
