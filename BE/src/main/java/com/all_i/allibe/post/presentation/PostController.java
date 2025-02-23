package com.all_i.allibe.post.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.application.PostService;
import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.dto.request.PostCreateRequest;
import com.all_i.allibe.post.dto.request.PostUpdateRequest;
import com.all_i.allibe.post.dto.response.PostDetailResponse;
import com.all_i.allibe.post.dto.response.MemberMealTimeDataResponse;
import com.all_i.allibe.post.dto.response.NearPostResponse;
import com.all_i.allibe.post.dto.response.AlbumResponse;
import com.all_i.allibe.post.dto.response.TagPicResponse;
import com.all_i.allibe.post.dto.response.TagExistResponse;
import com.all_i.allibe.s3.application.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final S3FileService s3FileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
            @AuthMember Member member,
            @RequestPart(value = "postCreateRequest") PostCreateRequest postCreateRequest,
            @RequestPart(value = "images") List<MultipartFile> images
    ) {
        List<String> imageUrls = uploadPostImages(images);
        Long postId = postService.createPost(postCreateRequest, imageUrls, member);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + postId))
                .build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable(name = "postId") Long postId,
            @AuthMember Member member
    ) {
        PostDetailResponse postDetailResponse = postService.getPost(member, postId);
        return ResponseEntity.ok().body(postDetailResponse);
    }

    @GetMapping
    public ResponseEntity<PageInfo<PostDetailResponse>> getPosts(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String pageToken,
            @AuthMember Member member
    ) {
        PageInfo<PostDetailResponse> postLists = postService.getPosts(member, tagName, pageToken);
        return ResponseEntity.ok().body(postLists);
    }

    @GetMapping("/me/{foodTag}")
    public ResponseEntity<PageInfo<PostDetailResponse>> getMyFoodTagPosts(
            @PathVariable(name = "foodTag") FoodTag foodTag,
            @RequestParam(required = false) String pageToken,
            @AuthMember Member member
    ){
        PageInfo<PostDetailResponse> postLists = postService.getMyFoodTagPosts(member, foodTag, pageToken);
        return ResponseEntity.ok().body(postLists);
    }

    @GetMapping("/{foodTag}/{memberId}")
    public ResponseEntity<PageInfo<PostDetailResponse>> getFoodTagPosts(
            @PathVariable(name = "foodTag") FoodTag foodTag,
            @PathVariable(name = "memberId") Long memberId,
            @RequestParam(required = false) String pageToken,
            @AuthMember Member member
    ){
        PageInfo<PostDetailResponse> postLists = postService.getFoodTagPosts(member, memberId, foodTag, pageToken);
        return ResponseEntity.ok().body(postLists);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody PostUpdateRequest postUpdateRequest,
            @AuthMember Member member
    ) {

        postService.updatePost(postId, postUpdateRequest, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId,
            @AuthMember Member member
    ) {

        postService.deletePost(postId, member);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/data")
    public ResponseEntity<List<MemberMealTimeDataResponse>> getMyPostsData(
            @AuthMember Member member
    ) {
        List<MemberMealTimeDataResponse> memberMealTimeDataResponse = postService.getPostCountByMealTime(member);
        return ResponseEntity.ok().body(memberMealTimeDataResponse);
    }

    @GetMapping("/{memberId}/data")
    public ResponseEntity<List<MemberMealTimeDataResponse>> getPostsData(
            @PathVariable(name = "memberId") Long memberId,
            @AuthMember Member member
    ) {
        List<MemberMealTimeDataResponse> memberMealTimeDataResponse = postService.getOtherPostCountByMealTime(memberId);
        return ResponseEntity.ok().body(memberMealTimeDataResponse);
    }

    /**
     *
     * 현재 GPS 위치 기반으로 x(1000)M내의 게시글을 y(20)개 조회한다.
     * 위치 정보를 넘기지 않으면 처음 회원가입시 등록한 위경도 기준으로 조회한다.
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<NearPostResponse>> getNearByPosts(
            @AuthMember Member member,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude
    ) {
        var posts = postService.findNearByPosts(
                member,
                latitude,
                longitude
        );
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/me/album")
    public ResponseEntity<List<AlbumResponse>> getMyAlbumData(
            @AuthMember Member member
    ){
        List<AlbumResponse> myAlbumResponses = postService.getMyAlbumData(member);
        return ResponseEntity.ok().body(myAlbumResponses);
    }

    @GetMapping("/{memberId}/album")
    public ResponseEntity<List<AlbumResponse>> getAlbumData(
            @AuthMember Member member,
            @PathVariable(name = "memberId") Long memberId
    ){
        List<AlbumResponse> myAlbumResponses = postService.getAlbumData(memberId);
        return ResponseEntity.ok().body(myAlbumResponses);
    }

    @GetMapping("/tag/{tagName}/me")
    public ResponseEntity<PageInfo<TagPicResponse>> getMyTagPosts(
            @AuthMember Member member,
            @RequestParam(required = false) String pageToken,
            @PathVariable(name = "tagName") String tagName
    ) {
        PageInfo<TagPicResponse> response = postService.getMyTagAlbums(
                member,
                tagName,
                pageToken
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/tag/{tagName}")
    public ResponseEntity<PageInfo<PostDetailResponse>> getOtherMembersTagPosts(
            @AuthMember Member member,
            @RequestParam(required = false) String pageToken,
            @PathVariable(name = "tagName") String tagName
    ) {
        PageInfo<PostDetailResponse> response = postService.getOthersTagAlbums(
                member,
                tagName,
                pageToken
        );
        return ResponseEntity.ok().body(response);
    }

    private List<String> uploadPostImages(List<MultipartFile> profileImages){
        String dirName = "post";
        return s3FileService.uploadFiles(profileImages, dirName);
    }
}
