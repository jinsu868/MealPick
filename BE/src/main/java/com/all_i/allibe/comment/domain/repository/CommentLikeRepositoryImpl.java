package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.dto.CommentLikeCount;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.comment.domain.QCommentLike.commentLike;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentLikeCount> getCommentLikeCounts(List<Long> commentIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CommentLikeCount.class,
                                commentLike.commentId,
                                commentLike.commentId.count().as("likeCount")
                        )
                )
                .from(commentLike)
                .where(commentLike.commentId.in(commentIds))
                .groupBy(commentLike.commentId)
                .fetch();
    }
}
