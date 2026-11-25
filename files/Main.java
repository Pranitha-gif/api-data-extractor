package com.example.extractor;

import java.nio.file.Path;
import java.util.List;

/**
 * api-data-extractor: pulls data from a REST API and saves it to a CSV file and a SQLite DB.
 *
 * Usage (all arguments optional):
 *   java -jar api-data-extractor.jar [apiUrl] [outputDir]
 */
public class Main {

    private static final String DEFAULT_URL = "https://jsonplaceholder.typicode.com/posts";
    private static final String DEFAULT_OUT_DIR = "output";

    public static void main(String[] args) {
        String url = args.length > 0 ? args[0] : DEFAULT_URL;
        Path outDir = Path.of(args.length > 1 ? args[1] : DEFAULT_OUT_DIR);

        try {
            System.out.println("Fetching data from " + url);
            List<Post> posts = new ApiClient().fetchPosts(url);
            System.out.println("Fetched " + posts.size() + " records");

            Path csvFile = outDir.resolve("posts.csv");
            new CsvWriter().write(posts, csvFile);
            System.out.println("Wrote CSV: " + csvFile.toAbsolutePath());

            Path dbFile = outDir.resolve("posts.db");
            int rows = new SqliteWriter().write(posts, dbFile);
            System.out.println("Loaded " + rows + " rows into SQLite: " + dbFile.toAbsolutePath());

            System.out.println("Done.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Extraction interrupted.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Extraction failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
