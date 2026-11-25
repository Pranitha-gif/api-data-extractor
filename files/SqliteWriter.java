package com.example.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/** Loads posts into a SQLite table. Re-running the job updates existing rows (upsert). */
public class SqliteWriter {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS posts (
                id      INTEGER PRIMARY KEY,
                user_id INTEGER NOT NULL,
                title   TEXT,
                body    TEXT
            )
            """;

    private static final String UPSERT = """
            INSERT INTO posts (id, user_id, title, body) VALUES (?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                user_id = excluded.user_id,
                title   = excluded.title,
                body    = excluded.body
            """;

    public int write(List<Post> posts, Path dbFile) throws IOException, SQLException {
        Files.createDirectories(dbFile.toAbsolutePath().getParent());

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile)) {
            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {
                st.execute(CREATE_TABLE);
            }

            try (PreparedStatement ps = conn.prepareStatement(UPSERT)) {
                for (Post p : posts) {
                    ps.setInt(1, p.id());
                    ps.setInt(2, p.userId());
                    ps.setString(3, p.title());
                    ps.setString(4, p.body());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        }
        return posts.size();
    }
}
