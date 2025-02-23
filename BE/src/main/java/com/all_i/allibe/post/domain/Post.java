package com.all_i.allibe.post.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Enumerated(EnumType.STRING)
    private MealTime mealTime;

    @Enumerated(EnumType.STRING)
    private FoodTag foodTag;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point point;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> postImages;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> tags;

    private Post(
            Long authorId,
            String title,
            String content,
            MealTime mealTime,
            FoodTag foodTag,
            Point point,
            List<String> postImages,
            List<String> tags
    ) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.mealTime = mealTime;
        this.foodTag = foodTag;
        this.point = point;
        this.postImages = postImages;
        this.tags = tags;
    }

    public static Post of(
            Long authorId,
            String title,
            String content,
            MealTime mealTime,
            FoodTag foodTag,
            Point point,
            List<String> postImages,
            List<String> tags
    ) {
        return new Post(
                authorId,
                title,
                content,
                mealTime,
                foodTag,
                point,
                postImages,
                tags
        );
    }

    public void update(
            String content,
            List<String> tags
    ) {
        this.content = content;
        this.tags = tags;
    }
}
