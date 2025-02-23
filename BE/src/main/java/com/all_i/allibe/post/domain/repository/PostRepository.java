package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.Post;
import com.all_i.allibe.post.dto.response.MemberMealTimeDataResponse;
import com.all_i.allibe.post.dto.query.TopTagQuery;
import com.all_i.allibe.post.dto.query.FrequentMealTimeQuery;
import com.all_i.allibe.post.dto.query.PostCountDayPeriodQuery;
import com.all_i.allibe.post.dto.query.TagPicQuery;
import com.all_i.allibe.post.dto.query.CommentCountQuery;
import com.all_i.allibe.post.dto.query.PostInfoQuery;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private static final int POST_PAGE_SIZE = 20;

    private final PostJpaRepository postJpaRepository;
    private final PostJpaRepositoryCustomImpl postJpaRepositoryCustomImpl;

    public PageInfo<PostInfoQuery> findAllPosts(
            String pageToken,
            String searchTag
    ) {
        var data = postJpaRepositoryCustomImpl.findAllPosts(
                pageToken,
                searchTag,
                POST_PAGE_SIZE
        );

        if (data.size() <= POST_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.postId());

        return PageInfo.of(nextPageToken, data, true);
    }

    public PageInfo<PostInfoQuery> findFoodTagPostsByMemberId(
            String pageToken,
            Long loginId,
            FoodTag foodTag
    ) {
        var data = postJpaRepositoryCustomImpl.findFoodTagPostsByMemberId(
                pageToken,
                loginId,
                foodTag,
                POST_PAGE_SIZE
        );

        if (data.size() <= POST_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.postId());

        return PageInfo.of(nextPageToken, data, true);
    }

    public List<CommentCountQuery> getPostCommentCounts(List<Long> postIds) {
        return postJpaRepositoryCustomImpl.getPostCommentCounts(postIds);
    }

    public List<MemberMealTimeDataResponse> getPostCountByMealTime(Long memberId) {
        return postJpaRepositoryCustomImpl.getPostCountByMemberId(memberId);
    }

    public PostInfoQuery getPostWithMember(Long postId) {
        return postJpaRepositoryCustomImpl.getPostWithMember(postId);
    }

    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    public Optional<Post> findById(long postId) {
        return postJpaRepository.findById(postId);
    }

    public void delete(Post post) {
        postJpaRepository.delete(post);
    }

    public boolean existsById(long postId) {
        return postJpaRepository.existsById(postId);
    }

    public Optional<TopTagQuery> findTopTagByMemberId(Long memberId) {
        return postJpaRepository.getTopTag(memberId);
    }

    public Optional<FrequentMealTimeQuery> findMostFrequentEatingTimeByFoodTag(
            Long memberId,
            FoodTag topTag
    ) {
        return postJpaRepository.getMostFrequentEatingTimeByFoodTag(
                memberId,
                topTag
        );
    }

    public List<PostCountDayPeriodQuery> getPostCountInPeriod(
            String tagName,
            LocalDateTime from,
            LocalDateTime now
    ) {
        return postJpaRepository.getAllPostByTagAndBetween(
                tagName,
                from,
                now
        );
    }

    public List<Post> findNearByPosts(
            Point currentMemberLocation,
            Double meterRange
    ) {
        return postJpaRepository.getNearByPosts(
                currentMemberLocation,
                meterRange,
                20
        );
    }

    public PageInfo<TagPicQuery> getTagPostsByMemberId(
            String pageToken,
            Long memberId,
            String tagName
    ) {
        var data = postJpaRepositoryCustomImpl.getTagPostsByMemberId(
                pageToken,
                memberId,
                tagName,
                POST_PAGE_SIZE
        );

        if (data.size() <= POST_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.postId());

        return PageInfo.of(nextPageToken, data, true);
    }

    public PageInfo<PostInfoQuery> getOthersTagPosts(
            String pageToken,
            Long memberId,
            String tagName
    ) {
        var data = postJpaRepositoryCustomImpl.getOthersTagPosts(
                pageToken,
                memberId,
                tagName,
                POST_PAGE_SIZE
        );

        if (data.size() <= POST_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.postId());

        return PageInfo.of(nextPageToken, data, true);
    }
}
