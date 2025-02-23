package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.dto.query.PostLikeCountQuery;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.post.domain.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostLikeCountQuery> getPostLikeCounts(List<Long> postIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PostLikeCountQuery.class,
                                postLike.postId,
                                postLike.postId.count().as("likeCount")
                        )
                )
                .from(postLike)
                .where(postLike.postId.in(postIds))
                .groupBy(postLike.postId)
                .fetch();
    }

    @Override
    public int countByPostId(Long postId) {
        return queryFactory
                .select(
                        postLike.count().intValue()
                )
                .from(postLike)
                .where(postLike.postId.eq(postId))
                .fetchOne();
    }
}
