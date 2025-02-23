package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.dto.CommentInfo;
import com.all_i.allibe.comment.dto.response.CommentResponse;
import com.all_i.allibe.member.domain.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.comment.domain.QComment.comment;
import static com.all_i.allibe.comment.domain.QCommentLike.commentLike;
import static com.all_i.allibe.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class CommentJpaRepositoryCustomImpl implements CommentJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentResponse> findAllRootComment(
            Long postId,
            String pageToken,
            int pageSize
    ) {
        return queryFactory.select(
                Projections.constructor(
                        CommentResponse.class,
                        comment.id,
                        comment.memberId,
                        comment.content,
                        comment.deleted,
                        commentLike.count().intValue(),
                        comment.createdAt
                ))
                .from(comment)
                .leftJoin(commentLike)
                .on(comment.id.eq(commentLike.commentId))
                .where(isInRange(pageToken),
                        comment.postId.eq(postId),
                        comment.parentId.isNull())
                .groupBy(comment.id)
                .orderBy(comment.id.asc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<CommentResponse> findAllChildComment(
            Long parentCommentId,
            String pageToken,
            int pageSize
    ) {
        return queryFactory.select(
                        Projections.constructor(
                                CommentResponse.class,
                                comment.id,
                                member.id,
                                comment.content,
                                comment.deleted,
                                commentLike.count().intValue(),
                                comment.createdAt
                        ))
                .from(comment)
                .leftJoin(commentLike)
                .on(comment.id.eq(commentLike.commentId))
                .leftJoin(member).on(comment.memberId.eq(member.id))
                .where(isInRange(pageToken),
                        comment.parentId.eq(parentCommentId))
                .groupBy(comment.id)
                .orderBy(comment.id.asc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<CommentInfo> findAllChildCommentWithMember(Long parentCommentId, String pageToken, int pageSize) {
        return queryFactory.select(
                Projections.constructor(
                        CommentInfo.class,
                        comment.id,
                        comment.memberId,
                        member.nickname,
                        member.profileImage,
                        comment.content,
                        comment.deleted,
                        comment.createdAt
                ))
                .from(comment)
                .leftJoin(member)
                .on(comment.memberId.eq(member.id))
                .where(comment.parentId.eq(parentCommentId))
                .groupBy(comment.id)
                .orderBy(comment.id.asc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public int countByParentCommentId(Long parentCommentId){
        return queryFactory
                .select(comment.count().intValue())
                .from(comment)
                .where(comment.parentId.eq(parentCommentId))
                .fetchOne();
    }

    @Override
    public int countByPostId(Long postId) {
        return queryFactory
                .select(
                        comment.count().intValue()
                )
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetchOne();
    }

    private BooleanExpression isInRange(String pageToken) {
        if (pageToken == null) {
            return null;
        }

        return comment.id.gt(Long.parseLong(pageToken));
    }
}
