package com.all_i.allibe.post.application;

import com.all_i.allibe.comment.domain.CommentLike;
import com.all_i.allibe.comment.domain.repository.CommentLikeRepository;
import com.all_i.allibe.comment.domain.repository.CommentRepository;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.domain.MealTime;
import com.all_i.allibe.post.domain.Post;
import com.all_i.allibe.post.domain.PostLike;
import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.repository.PostRepository;
import com.all_i.allibe.post.domain.repository.PostLikeRepository;
import com.all_i.allibe.post.domain.repository.PostLikeRepositoryCustomImpl;
import com.all_i.allibe.post.domain.repository.PostJdbcRepository;
import com.all_i.allibe.post.domain.repository.TagHistoryJdbcRepository;
import com.all_i.allibe.post.domain.repository.TagHistoryRepository;
import com.all_i.allibe.post.dto.query.PostInfoQuery;
import com.all_i.allibe.post.dto.query.PostLikeCountQuery;
import com.all_i.allibe.post.dto.query.CommentCountQuery;
import com.all_i.allibe.post.dto.query.AlbumQuery;
import com.all_i.allibe.post.dto.request.PostUpdateRequest;
import com.all_i.allibe.post.dto.response.TagResponse;
import com.all_i.allibe.post.dto.response.PostDetailResponse;
import com.all_i.allibe.post.dto.response.MemberMealTimeDataResponse;
import com.all_i.allibe.post.dto.response.NearPostResponse;
import com.all_i.allibe.post.dto.response.AlbumResponse;
import com.all_i.allibe.post.dto.response.TagPicResponse;
import com.all_i.allibe.post.dto.query.TagPicQuery;
import com.all_i.allibe.post.dto.response.TagExistResponse;
import com.all_i.allibe.post.dto.request.PostCreateRequest;
import com.all_i.allibe.tagrank.dto.PostCreateEvent;
import com.all_i.allibe.scrab.domain.Scrab;
import com.all_i.allibe.scrab.domain.repository.ScrabRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private static final Double METER_RANGE = 1000d;

    private final PostRepository postRepository;
    private final PostLikeRepositoryCustomImpl postLikeRepositoryCustomImpl;
    private final ScrabRepository scrabRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final PostJdbcRepository postJdbcRepository;
    private final TagHistoryJdbcRepository tagHistoryJdbcRepository;
    private final TagHistoryRepository tagHistoryRepository;


    @Transactional
    public Long createPost(
            PostCreateRequest postCreateRequest,
            List<String> imageUrls,
            Member author
    ) {
        Long authorId = author.getId();

        if (hasDuplicate(postCreateRequest.tags())){
            throw new BadRequestException(ErrorCode.DUPLICATE_TAG);
        }

        MealTime requestMealTime = convertToMealTime(postCreateRequest.requestTime());
        MealTime nowMealTime = convertToMealTime(LocalDateTime.now());

        if (!nowMealTime.equals(requestMealTime)) {
            throw new BadRequestException(ErrorCode.INCORRECT_MEAL_TIME);
        }

        Point postLocation = geometryFactory.createPoint(new Coordinate(
                postCreateRequest.longitude(),
                postCreateRequest.latitude())
        );
        postLocation.setSRID(4326);

        Post post = Post.of(
                authorId,
                postCreateRequest.title(),
                postCreateRequest.content(),
                requestMealTime,
                postCreateRequest.foodTag(),
                postLocation,
                imageUrls,
                postCreateRequest.tags()
        );

        Long postId = postRepository.save(post).getId();


        tagHistoryJdbcRepository.batchInsert(
                postId,
                postCreateRequest.tags()
        );

        eventPublisher.publishEvent(new PostCreateEvent(
                        post.getId(),
                        postCreateRequest.tags()
                                .stream()
                                .map(TagResponse::from)
                                .toList()
                )
        );

        return postId;
    }

    public PostDetailResponse getPost(
            Member loginMember,
            Long postId
    ) {
        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        Long loginId = loginMember.getId();

        PostInfoQuery postInfo = postRepository.getPostWithMember(postId);
        int commentCount = commentRepository.countByPostId(postId);
        int postLikeCount = postLikeRepositoryCustomImpl.countByPostId(postId);
        boolean isLiked = postLikeRepository.existsByMemberIdAndPostId(loginId, postId);
        boolean isBookmarked = scrabRepository.existsByMemberIdAndPostId(loginId, postId);


        PostDetailResponse postDetailResponse = PostDetailResponse.of(
                postInfo,
                commentCount,
                postLikeCount,
                isLiked,
                isBookmarked
        );

        return postDetailResponse;
    }

    public PageInfo<PostDetailResponse> getPosts(
            Member loginMember,
            String tagName,
            String pageToken
    ) {
        Long loginId = loginMember.getId();

        PageInfo<PostInfoQuery> postInfos = postRepository.findAllPosts(pageToken, tagName);

        List<Long> postIds = postInfos.data().stream()
                .map(PostInfoQuery::postId)
                .collect(Collectors.toList());

        List<Long> postLikes = postLikeRepository.findAllByMemberIdAndPostIdIn(loginId, postIds)
                .stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        List<Long> scrabs = scrabRepository.findAllScrabsByMemberId(loginId)
                .stream()
                .map(Scrab::getPostId)
                .collect(Collectors.toList());

        Map<Long, Long> postLikeCounts = postLikeRepository.getPostLikeCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                PostLikeCountQuery::postId,
                                PostLikeCountQuery::likeCount
                        ));
        Map<Long, Long> postCommentCounts = postRepository.getPostCommentCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                CommentCountQuery::postId,
                                CommentCountQuery::commentCount
                        ));

        List<PostDetailResponse> responses = new ArrayList<>();

        for (PostInfoQuery response : postInfos.data()) {
            PostDetailResponse newResponse = PostDetailResponse.of(
                    response,
                    postLikeCounts,
                    postCommentCounts,
                    postLikes,
                    scrabs
            );
            responses.add(newResponse);
        }

        return PageInfo.of(postInfos.pageToken(), responses, postInfos.hasNext());
    }

    public PageInfo<PostDetailResponse> getMyFoodTagPosts(
            Member loginMember,
            FoodTag foodTag,
            String pageToken
    ) {
        Long loginId = loginMember.getId();

        PageInfo<PostInfoQuery> postInfos = postRepository.findFoodTagPostsByMemberId(
                pageToken,
                loginId,
                foodTag
        );

        List<Long> postIds = postInfos.data().stream()
                .map(PostInfoQuery::postId)
                .collect(Collectors.toList());

        List<Long> postLikes = postLikeRepository.findAllByMemberIdAndPostIdIn(loginId, postIds)
                .stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        List<Long> scrabs = scrabRepository.findAllScrabsByMemberId(loginId)
                .stream()
                .map(Scrab::getPostId)
                .collect(Collectors.toList());

        Map<Long, Long> postLikeCounts = postLikeRepository.getPostLikeCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                PostLikeCountQuery::postId,
                                PostLikeCountQuery::likeCount
                        ));

        Map<Long, Long> postCommentCounts = postRepository.getPostCommentCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                CommentCountQuery::postId,
                                CommentCountQuery::commentCount
                        ));

        List<PostDetailResponse> responses = new ArrayList<>();

        for (PostInfoQuery response : postInfos.data()) {
            PostDetailResponse newResponse = PostDetailResponse.of(
                    response,
                    postLikeCounts,
                    postCommentCounts,
                    postLikes,
                    scrabs
            );
            responses.add(newResponse);
        }

        return PageInfo.of(postInfos.pageToken(), responses, postInfos.hasNext());
    }

    public PageInfo<PostDetailResponse> getFoodTagPosts(
            Member loginMember,
            Long memberId,
            FoodTag foodTag,
            String pageToken
    ) {

        PageInfo<PostInfoQuery> postInfos = postRepository.findFoodTagPostsByMemberId(
                pageToken,
                memberId,
                foodTag
        );

        List<Long> postIds = postInfos.data().stream()
                .map(PostInfoQuery::postId)
                .collect(Collectors.toList());

        List<Long> postLikes = postLikeRepository.findAllByMemberIdAndPostIdIn(loginMember.getId(), postIds)
                .stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        List<Long> scrabs = scrabRepository.findAllScrabsByMemberId(loginMember.getId())
                .stream()
                .map(Scrab::getPostId)
                .collect(Collectors.toList());

        Map<Long, Long> postLikeCounts = postLikeRepository.getPostLikeCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                PostLikeCountQuery::postId,
                                PostLikeCountQuery::likeCount
                        ));

        Map<Long, Long> postCommentCounts = postRepository.getPostCommentCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                CommentCountQuery::postId,
                                CommentCountQuery::commentCount
                        ));

        List<PostDetailResponse> responses = new ArrayList<>();

        for (PostInfoQuery response : postInfos.data()) {
            PostDetailResponse newResponse = PostDetailResponse.of(
                    response,
                    postLikeCounts,
                    postCommentCounts,
                    postLikes,
                    scrabs
            );
            responses.add(newResponse);
        }

        return PageInfo.of(postInfos.pageToken(), responses, postInfos.hasNext());
    }

    @Transactional
    public void updatePost(
            Long postId,
            PostUpdateRequest postUpdateRequest,
            Member author
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        if (hasDuplicate(postUpdateRequest.tags())){
            throw new BadRequestException(ErrorCode.DUPLICATE_TAG);
        }

        if (!post.getAuthorId().equals(author.getId())) {
            throw new BadRequestException(ErrorCode.NOT_MY_POST);
        }

        if (postUpdateRequest != null) {
            post.update(
                    postUpdateRequest.content(),
                    postUpdateRequest.tags()
            );
        } else {
            log.warn("Content is null");
        }

        tagHistoryJdbcRepository.batchDelete(postId);
        tagHistoryJdbcRepository.batchInsert(postId, postUpdateRequest.tags());
    }

    @Transactional
    public void deletePost(
            Long postId,
            Member member
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        if (!member.getId().equals(post.getAuthorId())) {
            throw new BadRequestException(ErrorCode.NOT_MY_POST);
        }

        tagHistoryJdbcRepository.batchDelete(postId);
        postRepository.delete(post);
    }

    public List<MemberMealTimeDataResponse> getPostCountByMealTime(Member loginMember) {
        List<MemberMealTimeDataResponse> postCount = postRepository.getPostCountByMealTime(loginMember.getId());

        Map<MealTime, Integer> postCountMap = new LinkedHashMap<>();
        for (MealTime mealTime : MealTime.values()) {
            postCountMap.put(mealTime, 0);
        }

        for (MemberMealTimeDataResponse memberMealTimeDataResponse : postCount) {
            postCountMap.put(memberMealTimeDataResponse.mealTime(), memberMealTimeDataResponse.count());
        }

        List<MemberMealTimeDataResponse> response = postCountMap
                .entrySet()
                .stream()
                .map(entry -> new MemberMealTimeDataResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return response;
    }

    public List<MemberMealTimeDataResponse> getOtherPostCountByMealTime(Long memberId) {
        List<MemberMealTimeDataResponse> postCount = postRepository.getPostCountByMealTime(memberId);

        Map<MealTime, Integer> postCountMap = new LinkedHashMap<>();
        for (MealTime mealTime : MealTime.values()) {
            postCountMap.put(mealTime, 0);
        }

        for (MemberMealTimeDataResponse memberMealTimeDataResponse : postCount) {
            postCountMap.put(memberMealTimeDataResponse.mealTime(), memberMealTimeDataResponse.count());
        }

        List<MemberMealTimeDataResponse> response = postCountMap
                .entrySet()
                .stream()
                .map(entry -> new MemberMealTimeDataResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return response;
    }

    public List<NearPostResponse> findNearByPosts(
            Member loginMember,
            Double latitude,
            Double longitude
    ) {
        Point currentMemberLocation = getCurrentLocation(loginMember, latitude, longitude);
        return postRepository.findNearByPosts(currentMemberLocation, METER_RANGE).stream()
                .map(NearPostResponse::from)
                .toList();
    }

    public List<AlbumResponse> getMyAlbumData(
            Member loginMember
    ){
        Long loginId = loginMember.getId();
        List<AlbumQuery> AlbumQuerys = postJdbcRepository.getAlbumDataByMemberId(loginId);

        Map<FoodTag, List<AlbumQuery>> foodTagMap = AlbumQuerys
                .stream()
                .collect(Collectors.groupingBy(AlbumQuery::foodTag));

        List<AlbumResponse> myAlbumResponses = foodTagMap.entrySet().stream()
                .map(entry -> {
                    FoodTag currentFoodTag = entry.getKey();
                    List<AlbumQuery> infos = entry.getValue();

                    LocalDateTime lastCreatedAt = infos.get(0).lastDate();
                    int postCount =  infos.get(0).postCount();

                    List<String> postImages = infos.stream()
                            .map(AlbumQuery::postImage)
                            .collect(Collectors.toList());

                    return new AlbumResponse(
                            currentFoodTag,
                            lastCreatedAt,
                            postCount,
                            postImages
                    );
                })
                .collect(Collectors.toList());

        return myAlbumResponses;
    }

    public List<AlbumResponse> getAlbumData(
            Long memberId
    ){
        List<AlbumQuery> AlbumQuerys = postJdbcRepository.getAlbumDataByMemberId(memberId);

        Map<FoodTag, List<AlbumQuery>> foodTagMap = AlbumQuerys
                .stream()
                .collect(Collectors.groupingBy(AlbumQuery::foodTag));

        List<AlbumResponse> albumResponses = foodTagMap.entrySet().stream()
                .map(entry -> {
                    FoodTag currentFoodTag = entry.getKey();
                    List<AlbumQuery> infos = entry.getValue();

                    LocalDateTime lastCreatedAt = infos.get(0).lastDate();
                    int postCount =  infos.get(0).postCount();

                    List<String> postImages = infos.stream()
                            .map(AlbumQuery::postImage)
                            .collect(Collectors.toList());

                    return new AlbumResponse(
                            currentFoodTag,
                            lastCreatedAt,
                            postCount,
                            postImages
                    );
                })
                .collect(Collectors.toList());

        return albumResponses;
    }

    public PageInfo<TagPicResponse> getMyTagAlbums(
            Member loginMember,
            String tagName,
            String pageToken
    ) {

        PageInfo<TagPicQuery> postTagInfo = postRepository.getTagPostsByMemberId(
                pageToken,
                loginMember.getId(),
                tagName
        );

        List<TagPicResponse> responses = new ArrayList<>();

        for (TagPicQuery response : postTagInfo.data()) {
            TagPicResponse newResponse = new TagPicResponse(
                    response.postId(),
                    response.postImages().get(0)
            );
            responses.add(newResponse);
        }

        return PageInfo.of(postTagInfo.pageToken(), responses, postTagInfo.hasNext());
    }

    public PageInfo<PostDetailResponse> getOthersTagAlbums(
            Member loginMember,
            String tagName,
            String pageToken
    ) {
        Long loginId = loginMember.getId();

        PageInfo<PostInfoQuery> postInfos = postRepository.getOthersTagPosts(
                pageToken,
                loginId,
                tagName
        );

        List<Long> postIds = postInfos.data()
                .stream()
                .map(PostInfoQuery::postId)
                .collect(Collectors.toList());
        List<Long> postLikes = postLikeRepository.findAllByMemberIdAndPostIdIn(loginId, postIds)
                .stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());
        List<Long> scrabs = scrabRepository.findAllScrabsByMemberId(loginId)
                .stream()
                .map(Scrab::getPostId)
                .collect(Collectors.toList());

        Map<Long, Long> postLikeCounts = postLikeRepository.getPostLikeCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                PostLikeCountQuery::postId,
                                PostLikeCountQuery::likeCount
                        ));
        Map<Long, Long> postCommentCounts = postRepository.getPostCommentCounts(postIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                CommentCountQuery::postId,
                                CommentCountQuery::commentCount
                        ));

        List<PostDetailResponse> responses = new ArrayList<>();

        for (PostInfoQuery response : postInfos.data()) {
            PostDetailResponse newResponse = PostDetailResponse.of(
                    response,
                    postLikeCounts,
                    postCommentCounts,
                    postLikes,
                    scrabs
            );
            responses.add(newResponse);
        }

        return PageInfo.of(postInfos.pageToken(), responses, postInfos.hasNext());
    }

    public TagExistResponse getTagExist(String tagName) {
        boolean isExist = tagHistoryRepository.existsByTagName(tagName);
        return new TagExistResponse(
                tagName,
                isExist
        );
    }

    private  MealTime convertToMealTime(LocalDateTime localDateTime) {
        int hour = localDateTime.getHour();
        if (hour >= 0 && hour < 10) {
            return MealTime.MORNING;
        } else if (hour >= 10 && hour < 17) {
            return MealTime.LUNCH;
        } else  {
            return MealTime.DINNER;
        }
    }

    private Point getCurrentLocation(
            Member loginMember,
            Double latitude,
            Double longitude
    ) {
        Point currentLocation;
        if (latitude == null || longitude == null) {
            currentLocation = geometryFactory.createPoint(new Coordinate(
                    loginMember.getLongitude(),
                    loginMember.getLatitude())
            );
            currentLocation.setSRID(4326);
            return currentLocation;
        }

        currentLocation = geometryFactory.createPoint(new Coordinate(
                longitude,
                latitude)
        );
        currentLocation.setSRID(4326);
        return currentLocation;
    }

    private boolean hasDuplicate(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        Set<String> uniqueTags = new HashSet<>();
        for (String tag : tags) {
            if (!uniqueTags.add(tag)) {
                return true;
            }
        }
        return false;
    }
}
