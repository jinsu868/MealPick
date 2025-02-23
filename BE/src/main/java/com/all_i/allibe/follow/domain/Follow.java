package com.all_i.allibe.follow.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "following_id",
                "follower_id"}
        )
    }
//    indexes = {
//            @Index(
//                    name = "idx_follower_id",
//                    columnList = "follower_id"
//            ),
//            @Index(
//                    name = "idx_following_id",
//                    columnList = "following_id"
//            )
//    }
)
public class Follow extends BaseEntity {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "following_id")
    private Long followingId;

    @Column(name = "follower_id")
    private Long followerId;

    public static Follow create(Long following, Long follower) {
        return new Follow(
                following,
                follower
        );
    }

    private Follow(
            Long following,
            Long follower
    ) {
        this.followingId = following;
        this.followerId = follower;
    }
}
