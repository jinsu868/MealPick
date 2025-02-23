package com.all_i.allibe.scrab.domain.repository;

import com.all_i.allibe.scrab.dto.ScrabInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.post.domain.QPost.post;
import static com.all_i.allibe.scrab.domain.QScrab.scrab;

@Repository
@RequiredArgsConstructor
public class ScrabRepositoryCustomImpl implements ScrabRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ScrabInfo> getMyScrabs(Long loginId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ScrabInfo.class,
                                scrab.postId,
                                post.title,
                                post.postImages,
                                post.foodTag,
                                post.mealTime,
                                post.tags
                        )
                )
                .from(scrab)
                .leftJoin(post)
                .on(scrab.postId.eq(post.id))
                .where(scrab.memberId.eq(loginId))
                .fetch();
    }
}
