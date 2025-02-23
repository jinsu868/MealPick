package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.dto.query.CommentCountQuery;
import com.all_i.allibe.post.dto.query.PostInfoQuery;
import com.all_i.allibe.post.dto.query.TopTagQuery;
import com.all_i.allibe.post.dto.query.FrequentMealTimeQuery;
import com.all_i.allibe.post.dto.response.MemberMealTimeDataResponse;
import com.all_i.allibe.post.dto.query.TagPicQuery;
import com.all_i.allibe.post.dto.query.PostCountDayPeriodQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostJpaRepositoryCustom {

    List<PostInfoQuery> findAllPosts(
            String pageToken,
            String searchTag,
            int pageSize
    );

    List<CommentCountQuery> getPostCommentCounts(List<Long> postIds);

    PostInfoQuery getPostWithMember(Long postId);

    Optional<TopTagQuery> getTopTag(Long memberId);

    Optional<FrequentMealTimeQuery> getMostFrequentEatingTimeByFoodTag(
            Long memberId,
            FoodTag topTag
    );

    List<MemberMealTimeDataResponse> getPostCountByMemberId(Long memberId);

    List<PostCountDayPeriodQuery> getAllPostByTagAndBetween(
            String tagName,
            LocalDateTime oneWeekAgo,
            LocalDateTime now
    );

    List<PostInfoQuery> findFoodTagPostsByMemberId(
            String pageToken,
            Long memberId,
            FoodTag foodTag,
            int pageSize
    );

    List<TagPicQuery> getTagPostsByMemberId(
            String pageToken,
            Long memberId,
            String tagName,
            int pageSize
    );

    List<PostInfoQuery> getOthersTagPosts(
            String pageToken,
            Long memberId,
            String tagName,
            int pageSize
    );
}
