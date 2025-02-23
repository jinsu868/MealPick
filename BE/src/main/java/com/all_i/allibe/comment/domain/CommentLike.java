package com.all_i.allibe.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "member_id",
                                "comment_id"
                        }
                )
        }
)
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "comment_id")
    private Long commentId;

    public static CommentLike createCommentLike(
            Long memberId,
            Long commentId
    ) {
        return new CommentLike(
                memberId,
                commentId
        );
    }

    private CommentLike(
            Long memberId,
            Long commentId
    ) {
        this.memberId = memberId;
        this.commentId = commentId;
    }
}
