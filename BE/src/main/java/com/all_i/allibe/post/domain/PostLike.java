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
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "member_id")
    private Long memberId;

    private PostLike(
            Long postId,
            Long memberId
    ) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static PostLike createPostLike(
            Long postId,
            Long memberId
    ) {
        return new PostLike(
                postId,
                memberId
        );
    }
}
