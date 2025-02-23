package com.all_i.allibe.scrab.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Scrab extends BaseEntity {

    @Id
    @Column(name = "scrab_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "post_id")
    private Long postId;

    private Scrab(
            Long memberId,
            Long postId
    ) {
        this.memberId = memberId;
        this.postId = postId;
    }

    public static Scrab createScrab(
            Long memberId,
            Long postId
    ) {
        return new Scrab(
                memberId,
                postId
        );
    }

}
