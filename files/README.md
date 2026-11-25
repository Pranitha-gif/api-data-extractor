# api-data-extractor

Simple base project: pulls data from a REST API and saves it to a CSV file and a SQLite database.

## Requirements
- Java 17+
- Maven 3.8+
- Internet access (to call the API and download dependencies)

## Project layout
```
api-data-extractor/
├── pom.xml
└── src/main/java/com/example/extractor/
    ├── Main.java          # entry point: fetch -> CSV -> DB
    ├── ApiClient.java     # HTTP call with retry/backoff
    ├── Post.java          # data model (record)
    ├── CsvWriter.java     # writes output/posts.csv
    └── SqliteWriter.java  # upserts into output/posts.db
```

## Run
```bash
mvn compile exec:java
```

Optional arguments: API URL and output folder.
```bash
mvn compile exec:java -Dexec.args="https://jsonplaceholder.typicode.com/posts out"
```

## Check the result
```bash
head output/posts.csv
sqlite3 output/posts.db "SELECT COUNT(*) FROM posts;"
```

## How it works
1. `ApiClient` sends a GET request (3 attempts, 1s then 2s backoff) and parses the JSON array.
2. `CsvWriter` writes the records to `output/posts.csv`.
3. `SqliteWriter` upserts them into `output/posts.db`, so re-running never creates duplicates.

## Ideas for next steps
- Pagination and API-key headers
- Incremental loads (only fetch new records)
- Config file instead of command-line arguments
- Unit tests with a mocked HTTP server
