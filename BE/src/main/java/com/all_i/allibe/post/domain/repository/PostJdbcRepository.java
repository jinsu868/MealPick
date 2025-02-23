package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.dto.query.AlbumQuery;
import com.all_i.allibe.post.dto.response.RookieMenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<AlbumQuery> getAlbumDataByMemberId(Long loginId) {
        String sql = """
                SELECT
                    p.id,
                    p.food_tag,
                    p.created_at,
                    p.food_tag_count,
                    p.post_image
                FROM (
                    SELECT
                        id,
                        food_tag,
                        created_at,
                        JSON_UNQUOTE(JSON_EXTRACT(post_images, '$[0]')) as post_image,
                        COUNT(*) OVER (PARTITION BY food_tag) AS food_tag_count,
                        ROW_NUMBER() OVER (PARTITION BY food_tag
                                           ORDER BY created_at DESC)
                                           AS row_num
                    FROM post
                    WHERE author_id = ?
                ) p
                WHERE 
                    row_num <= 3
                ORDER BY
                    food_tag;
                """;
        return jdbcTemplate.query(
                sql,
                new Object[]{loginId},
                new RowMapper<AlbumQuery>() {
                    @Override
                    public AlbumQuery mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new AlbumQuery(
                                rs.getLong("id"),
                                FoodTag.valueOf(rs.getString("food_tag")),
                                rs.getTimestamp("created_at").toLocalDateTime(),
                                rs.getInt("food_tag_count"),
                                rs.getString("post_image")
                        );
                    }
                });
    }

    public List<RookieMenuResponse> getTopTrendingTagsMonth(
            LocalDateTime now,
            LocalDateTime monthAgo,
            LocalDateTime twoMonthsAgo
    ){
        String sql = """
                SELECT
                    c1.tag_name,
                    (COALESCE(c1.current_count,0) - COALESCE(c2.past_count, 0)) AS increase_count
                    
                FROM (
                    SELECT
                        t.tag_name,
                        COUNT(*) AS current_count
                    FROM 
                        tag_history t
                    WHERE 
                        t.created_at BETWEEN ? AND ?
                    GROUP BY 
                        t.tag_name
                ) c1
                LEFT JOIN (
                    SELECT
                        t.tag_name,
                        COUNT(*) AS past_count
                    FROM
                        tag_history t
                    WHERE
                        t.created_at BETWEEN ? AND ?
                    GROUP BY
                        t.tag_name
                ) c2
                ON 
                    c1.tag_name = c2.tag_name
                ORDER BY
                    increase_count DESC
                LIMIT 3;
                """;
        return jdbcTemplate.query(
                sql,
                new Object[]{Timestamp.valueOf(monthAgo), Timestamp.valueOf(now), Timestamp.valueOf(twoMonthsAgo), Timestamp.valueOf(monthAgo)},
                new RowMapper<RookieMenuResponse>() {
                    @Override
                    public RookieMenuResponse mapRow(ResultSet rs, int rowNum) throws SQLException {

                        return new RookieMenuResponse(
                                rs.getString("tag_name"),
                                rs.getInt("increase_count")
                        );
                    }
                }
        );
    }
}
