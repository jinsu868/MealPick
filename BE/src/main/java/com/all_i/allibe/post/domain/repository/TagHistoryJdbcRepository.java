package com.all_i.allibe.post.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagHistoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(
            Long postId,
            List<String> tagNames
    ) {
        String sql = "INSERT INTO tag_history (post_id, tag_name, created_at, modified_at) VALUES (?, ?, NOW(), NOW())";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String tagName = tagNames.get(i);
                ps.setLong(1, postId);
                ps.setString(2, tagName);
            }

            @Override
            public int getBatchSize() {
                return tagNames.size();
            }
        });
    }

    public void batchDelete(Long postId) {
        String sql = "DELETE FROM tag_history WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }
}
