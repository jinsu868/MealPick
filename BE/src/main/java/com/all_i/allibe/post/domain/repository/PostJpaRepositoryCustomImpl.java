package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.dto.query.CommentCountQuery;
import com.all_i.allibe.post.dto.query.PostInfoQuery;
import com.all_i.allibe.post.dto.query.TopTagQuery;
import com.all_i.allibe.post.dto.query.FrequentMealTimeQuery;
import com.all_i.allibe.post.dto.response.MemberMealTimeDataResponse;
import com.all_i.allibe.post.dto.query.PostCountDayPeriodQuery;
import com.all_i.allibe.post.dto.query.TagPicQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.all_i.allibe.comment.domain.QComment.comment;
import static com.all_i.allibe.member.domain.QMember.member;
import static com.all_i.allibe.post.domain.QPost.post;
import static com.all_i.allibe.post.domain.QTagHistory.tagHistory;

@Repository
@RequiredArgsConstructor
public class PostJpaRepositoryCustomImpl implements PostJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostInfoQuery> findAllPosts(
            String pageToken,
            String searchTag,
            int pageSize
    ) {
        return queryFactory.select(
                        Projections.constructor(
                                PostInfoQuery.class,
                                post.id,
                                post.postImages,
                                post.tags,
                                post.createdAt,
                                post.authorId,
                                member.profileImage,
                                member.nickname,
                                post.title,
                                post.content,
                                post.mealTime,
                                post.foodTag
                        ))
                .from(post)
                .leftJoin(member).on(post.authorId.eq(member.id))
                .where(isInRange(pageToken))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<CommentCountQuery> getPostCommentCounts(List<Long> postIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CommentCountQuery.class,
                                comment.postId,
                                comment.count().as("commentCount")
                        )
                )
                .from(comment)
                .where(comment.postId.in(postIds))
                .groupBy(comment.postId)
                .fetch();
    }

    @Override
    public PostInfoQuery getPostWithMember(Long postId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PostInfoQuery.class,
                                post.id,
                                post.postImages,
                                post.tags,
                                post.createdAt,
                                post.authorId,
                                member.profileImage,
                                member.nickname,
                                post.title,
                                post.content,
                                post.mealTime,
                                post.foodTag
                        )
                )
                .from(post)
                .leftJoin(member)
                .on(post.authorId.eq(member.id))
                .where(post.id.eq(postId))
                .fetchOne();
    }

    @Override
    public Optional<TopTagQuery> getTopTag(Long memberId) {
        return Optional.ofNullable(queryFactory.select(
                Projections.constructor(
                        TopTagQuery.class,
                        post.foodTag))
                .from(post)
                .where(post.authorId.eq(memberId))
                .groupBy(post.foodTag)
                .orderBy(post.foodTag.count().desc())
                .limit(1)
                .fetchOne());
    }

    @Override
    public Optional<FrequentMealTimeQuery> getMostFrequentEatingTimeByFoodTag(
            Long memberId,
            FoodTag topTag
    ) {
        return Optional.ofNullable(queryFactory.select(
                Projections.constructor(
                        FrequentMealTimeQuery.class,
                        post.mealTime))
                .from(post)
                        .leftJoin(member).on(post.authorId.eq(memberId))
                .where(post.foodTag.eq(topTag))
                .groupBy(post.mealTime)
                .orderBy(post.mealTime.count().desc())
                .limit(1)
                .fetchOne()
        );
    }

    @Override
    public List<MemberMealTimeDataResponse> getPostCountByMemberId(Long loginId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                MemberMealTimeDataResponse.class,
                                post.mealTime,
                                post.id.count().intValue()
                        )
                )
                .from(post)
                .where(post.authorId.eq(loginId))
                .groupBy(post.mealTime)
                .fetch();
    }

    @Override
    public List<PostCountDayPeriodQuery> getAllPostByTagAndBetween(
            String tagName,
            LocalDateTime from,
            LocalDateTime now
    ) {
        var allDatesInRange = generateDateRange(from.toLocalDate(), now.toLocalDate());
        var dayExpression = Expressions.stringTemplate(
                "DATE_FORMAT({0}, '%Y-%m-%d')",
                post.createdAt
        );

        List<Tuple> postCounts = queryFactory
                .select(
                        dayExpression,
                        tagHistory.tagName,
                        post.id.count().intValue()
                )
                .from(tagHistory)
                .leftJoin(post)
                .on(post.id.eq(tagHistory.postId))
                .where(post.createdAt.between(from, now)
                        .and(tagHistory.tagName.eq(tagName)))
                .groupBy(dayExpression)
                .fetch();

        return allDatesInRange.stream()
                .map(date -> {
                    String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    Tuple matchingPostCount = postCounts.stream()
                            .filter(postCount -> dateString.equals(postCount.get(dayExpression)))
                            .findFirst()
                            .orElse(null);

                    if (matchingPostCount != null) {
                        return new PostCountDayPeriodQuery(
                                dateString,
                                matchingPostCount.get(tagHistory.tagName),
                                matchingPostCount.get(post.id.count().intValue())
                        );
                    } else {
                        return new PostCountDayPeriodQuery(
                                dateString,
                                tagName,
                                0
                        );
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PostInfoQuery> findFoodTagPostsByMemberId(
            String pageToken,
            Long loginId,
            FoodTag foodTag,
            int pageSize
    ) {
        return queryFactory.select(
                        Projections.constructor(
                                PostInfoQuery.class,
                                post.id,
                                post.postImages,
                                post.tags,
                                post.createdAt,
                                post.authorId,
                                member.profileImage,
                                member.nickname,
                                post.title,
                                post.content,
                                post.mealTime,
                                post.foodTag
                        ))
                .from(post)
                .leftJoin(member)
                .on(post.authorId.eq(member.id))
                .where(post.authorId.eq(loginId),
                        post.foodTag.eq(foodTag),
                        isInRange(pageToken))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<TagPicQuery> getTagPostsByMemberId(
            String pageToken,
            Long memberId,
            String tagName,
            int pageSize
    ) {
        return queryFactory
                .select(
                        Projections.constructor(
                                TagPicQuery.class,
                                post.id,
                                post.postImages
                        )
                )
                .from(post)
                .leftJoin(tagHistory)
                .on(post.id.eq(tagHistory.postId))
                .where(tagHistory.tagName.eq(tagName),
                        post.authorId.eq(memberId),
                        isInRange(pageToken))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<PostInfoQuery> getOthersTagPosts(
            String pageToken,
            Long memberId,
            String tagName,
            int pageSize
    ) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PostInfoQuery.class,
                                post.id,
                                post.postImages,
                                post.tags,
                                post.createdAt,
                                post.authorId,
                                member.profileImage,
                                member.nickname,
                                post.title,
                                post.content,
                                post.mealTime,
                                post.foodTag
                        )
                )
                .from(post)
                .leftJoin(member)
                .on(member.id.eq(post.authorId))
                .leftJoin(tagHistory)
                .on(post.id.eq(tagHistory.postId))
                .where(post.authorId.ne(memberId),
                        tagHistory.tagName.eq(tagName),
                        isInRange(pageToken))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize + 1)
                .fetch();
    }


    private BooleanExpression isInRange(String pageToken) {
        if (pageToken == null) {
            return null;
        }

        return post.id.lt(Long.parseLong(pageToken));
    }

    private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());
    }
}
