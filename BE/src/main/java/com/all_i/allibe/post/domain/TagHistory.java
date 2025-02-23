package com.all_i.allibe.post.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TagHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String tagName;

    private TagHistory(
            Long postId,
            String tagName
    ) {
        this.postId = postId;
        this.tagName = tagName;
    }

    public static TagHistory of(
            Long postId,
            String tagName
    ) {
        return new TagHistory(
                postId,
                tagName
        );
    }
}
