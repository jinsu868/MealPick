package com.all_i.allibe.comment.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import static com.all_i.allibe.common.exception.ErrorCode.NOT_MY_COMMENT;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comment SET deleted = true WHERE comment_id = ?")
public class Comment extends BaseEntity {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "parent_id")
    private Long parentId;

    private String content;

    private boolean deleted;

    public static Comment createComment(
            Long postId,
            Long memberId,
            Long parentId,
            String content
    ) {
        return new Comment(
                postId,
                memberId,
                parentId,
                content
        );
    }

    public static Comment createComment(
            Long postId,
            Long memberId,
            String content
    ) {
        return new Comment(
                postId,
                memberId,
                null,
                content
        );
    }

    private Comment(
            Long postId,
            Long memberId,
            Long parentId,
            String content
    ) {
        this.postId = postId;
        this.memberId = memberId;
        this.parentId = parentId;
        this.content = content;
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public void validateOwner(Member member) {
        if (member.getId() != memberId) {
            throw new BadRequestException(NOT_MY_COMMENT);
        }
    }

    public void update(String content) {
        this.content = content;
    }
}
