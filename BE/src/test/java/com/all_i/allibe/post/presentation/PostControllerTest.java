package com.all_i.allibe.post.presentation;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.application.PostService;
import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;
import com.all_i.allibe.post.dto.request.PostCreateRequest;
import com.all_i.allibe.post.dto.request.PostUpdateRequest;
import com.all_i.allibe.post.dto.response.*;
import com.all_i.allibe.s3.application.S3FileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(PostController.class)
class PostControllerTest extends ControllerTest {

    @MockitoBean
    PostService postService;

    @MockitoBean
    S3FileService s3FileService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글을 생성한다.")
    void test_create_post() throws Exception {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        LocalDateTime now = LocalDateTime.now();
        PostCreateRequest request = new PostCreateRequest(
                "test title",
                "test content",
                FoodTag.ASIAN,
                37.5665,
                126.9780,
                tags,
                now
        );

        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile requestPart = new MockMultipartFile(
                "postCreateRequest",
                "",
                "application/json",
                requestJson.getBytes()
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "images",
                "test.jpg",
                "multipart/form-data",
                "test image content".getBytes()
        );

        given(postService.createPost(any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(multipart("/api/v1/posts")
                        .file(requestPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(postService).createPost(any(), any(), any());
    }

    @Test
    @DisplayName("게시물 한개를 조회합니다")
    void test_get_post() throws Exception {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        List<String> postImages = Arrays.asList("image1", "image2", "image3");
        Long postId = 1L;

        var expectedPost = new PostDetailResponse(
                1L,
                LocalDateTime.now(),
                1001L,
                "https://example.com/user123.jpg",
                "user123",
                "Test Title 1",
                "Test content for post 1",
                MealTime.LUNCH,
                FoodTag.ASIAN,
                postImages,
                tags,
                2,
                10,
                true,
                true
        );

        given(postService.getPost(any(),any()))
                .willReturn(expectedPost);

        MvcResult result = mockMvc
                .perform(get("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PostDetailResponse>() {}
        );

        Assertions.assertThat(response).isEqualTo(expectedPost);
    }

    @Test
    @DisplayName("모든 게시물 목록을 가져옵니다.")
    void test_get_posts() throws Exception {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        List<String> postImages = Arrays.asList("image1", "image2", "image3");

        var expectedPosts = List.of(
                new PostDetailResponse(
                        1L,
                        LocalDateTime.now(),
                        1001L,
                        "https://example.com/user123.jpg",
                        "user123",
                        "Test Title 1",
                        "Test content for post 1",
                        MealTime.LUNCH,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        2,
                        10,
                        true,
                        true
                ),
                new PostDetailResponse(
                        1L,
                        LocalDateTime.now(),
                        1001L,
                        "https://example.com/user123.jpg",
                        "user123",
                        "Test Title 1",
                        "Test content for post 1",
                        MealTime.LUNCH,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        2,
                        10,
                        true,
                        true
                )
        );

        String pageToken = "page1";
        Boolean hasNext = true;

        PageInfo<PostDetailResponse> pageInfo = PageInfo.of(pageToken, expectedPosts, hasNext);

        given(postService.getPosts(any(), any(), any())).willReturn(pageInfo);

        MvcResult result = mockMvc.perform(get("/api/v1/posts")
                        .param("tagName", "tag1")
                        .param("pageToken", "page1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostDetailResponse>>() {}
        );

        Assertions.assertThat(response.data()).isEqualTo(expectedPosts);
        Assertions.assertThat(response.pageToken()).isEqualTo(pageToken);
        Assertions.assertThat(response.hasNext()).isEqualTo(hasNext);
    }

    @Test
    @DisplayName("게시글 내용과 태그를 수정합니다.")
    void test_update_post() throws Exception {
        Long postId = 1L;
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        var request = new PostUpdateRequest(
                "test content",
                tags
        );

        doNothing().when(postService).updatePost(any(),any(),any());

        mockMvc.perform(patch("/api/v1/posts/" + postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(postService).updatePost(any(),any(),any());
    }

    @Test
    @DisplayName("게시글을 삭제합니다.")
    void test_delete_post() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).deletePost(any(),any());

        mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(postService).deletePost(any(),any());
    }

    @Test
    @DisplayName("아침, 점심, 저녁 별 게시글 수를 보여줍니다.")
    void test_get_my_posts_data() throws Exception {
        Long authorId = 1L;

        var expectedPosts = List.of(
                new MemberMealTimeDataResponse(
                        MealTime.MORNING,
                        0
                ),
                new MemberMealTimeDataResponse(
                        MealTime.LUNCH,
                        1
                ),
                new MemberMealTimeDataResponse(
                        MealTime.DINNER,
                        3
                )
        );

        List<MemberMealTimeDataResponse> memberMealTimeDataRespons = List.of(
                new MemberMealTimeDataResponse(
                        MealTime.MORNING,
                        0
                ),
                new MemberMealTimeDataResponse(
                        MealTime.LUNCH,
                        1
                ),
                new MemberMealTimeDataResponse(
                        MealTime.DINNER,
                        3
                )
        );

        given(postService.getPostCountByMealTime(any(Member.class)))
                .willReturn(expectedPosts);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/data")
                    .principal(()->String.valueOf(authorId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<MemberMealTimeDataResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expectedPosts);
    }

    @Test
    @DisplayName("다른 사람의 아침, 점심, 저녁 별 게시글 수를 보여줍니다.")
    void test_get_others_posts_data() throws Exception {
        Long authorId = 1L;

        var expectedPosts = List.of(
                new MemberMealTimeDataResponse(
                        MealTime.MORNING,
                        0
                ),
                new MemberMealTimeDataResponse(
                        MealTime.LUNCH,
                        1
                ),
                new MemberMealTimeDataResponse(
                        MealTime.DINNER,
                        3
                )
        );

        given(postService.getPostCountByMealTime(any(Member.class)))
                .willReturn(expectedPosts);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/data")
                        .principal(()->String.valueOf(authorId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<MemberMealTimeDataResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expectedPosts);
    }

    @Test
    @DisplayName("내가 올린 FoodTag 별 게시물들을 조회합니다")
    void test_get_my_food_tag_posts() throws Exception {
        List<Long> tagIds = Arrays.asList(1L, 2L, 3L);
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        List<String> postImages = Arrays.asList("image1", "image2", "image3");

        var expectedPosts = List.of(
                new PostDetailResponse(
                        1L,
                        LocalDateTime.now(),
                        1001L,
                        "https://example.com/user123.jpg",
                        "user123",
                        "Test Title 1",
                        "Test content for post 1",
                        MealTime.LUNCH,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        2,
                        10,
                        true,
                        true
                ),
                new PostDetailResponse(
                        2L,
                        LocalDateTime.now(),
                        1002L,
                        "https://example.com/user456.jpg",
                        "user456",
                        "Test Title 2",
                        "Test content for post 2",
                        MealTime.DINNER,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        3,
                        5,
                        false,
                        false
                )
        );

        String pageToken = "page1";
        Boolean hasNext = true;

        PageInfo<PostDetailResponse> pageInfo = PageInfo.of(pageToken, expectedPosts, hasNext);

        given(postService.getMyFoodTagPosts(any(), any(), any())).willReturn(pageInfo);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/{foodTag}", "ASIAN")
                        .param("pageToken", "page1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostDetailResponse>>() {}
        );

        Assertions.assertThat(response.data()).isEqualTo(expectedPosts);
        Assertions.assertThat(response.pageToken()).isEqualTo(pageToken);
        Assertions.assertThat(response.hasNext()).isEqualTo(hasNext);
    }

    @Test
    @DisplayName("타인이 올린 FoodTag 별 게시물들을 조회합니다")
    void test_get_food_tag_posts() throws Exception {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        List<String> postImages = Arrays.asList("image1", "image2", "image3");

        var expectedPosts = List.of(
                new PostDetailResponse(
                        1L,
                        LocalDateTime.now(),
                        1001L,
                        "https://example.com/user123.jpg",
                        "user123",
                        "Test Title 1",
                        "Test content for post 1",
                        MealTime.LUNCH,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        2,
                        10,
                        true,
                        true
                ),
                new PostDetailResponse(
                        2L,
                        LocalDateTime.now(),
                        1002L,
                        "https://example.com/user456.jpg",
                        "user456",
                        "Test Title 2",
                        "Test content for post 2",
                        MealTime.DINNER,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        3,
                        5,
                        false,
                        false
                )
        );

        String pageToken = "page1";
        Boolean hasNext = true;

        PageInfo<PostDetailResponse> pageInfo = PageInfo.of(pageToken, expectedPosts, hasNext);

        given(postService.getMyFoodTagPosts(any(), any(), any())).willReturn(pageInfo);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/{foodTag}", "ASIAN")
                        .param("pageToken", "page1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostDetailResponse>>() {}
        );

        Assertions.assertThat(response.data()).isEqualTo(expectedPosts);
        Assertions.assertThat(response.pageToken()).isEqualTo(pageToken);
        Assertions.assertThat(response.hasNext()).isEqualTo(hasNext);
    }

    @Test
    @DisplayName("위치 기반으로 포스트를 가져올 수 있다.")
    void can_retrieve_post_within_distance() throws Exception {
        var expect = List.of(
                new NearPostResponse(
                        1L,
                        "test-title1"
                ),
                new NearPostResponse(
                        2L,
                        "test-title2"
                )
        );

        given(postService.findNearByPosts(any(), any(), any()))
                .willReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/nearby")
                        .param("longitude", "127.12232")
                        .param("latitude", "32.123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<NearPostResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("음식 태그별 내 게시물 정보를 확인합니다.")
    void test_get_my_album_data() throws Exception {
        List<AlbumResponse> expect = List.of(
                new AlbumResponse(
                        FoodTag.KOREAN,
                        LocalDateTime.of(2024, 2, 16, 10, 0),
                        5,
                        List.of("image1.jpg", "image2.jpg")),
                new AlbumResponse(
                        FoodTag.JAPANESE,
                        LocalDateTime.of(2024, 2, 15, 15, 30),
                        3,
                        List.of("image3.jpg"))
        );

        given(postService.getMyAlbumData(any()))
                .willReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/album"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AlbumResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("음식 태그별 타인의 게시물 정보를 확인합니다.")
    void test_get_album_data() throws Exception {
        List<AlbumResponse> expect = List.of(
                new AlbumResponse(
                        FoodTag.KOREAN,
                        LocalDateTime.of(2024, 2, 16, 10, 0),
                        5,
                        List.of("image1.jpg", "image2.jpg")),
                new AlbumResponse(
                        FoodTag.JAPANESE,
                        LocalDateTime.of(2024, 2, 15, 15, 30),
                        3,
                        List.of("image3.jpg"))
        );

        given(postService.getMyAlbumData(any()))
                .willReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/me/album"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AlbumResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("Tag 별 내 게시물들을 조회합니다.")
    void test_get_my_tag_posts() throws Exception {

        var expectedPosts = List.of(
                new TagPicResponse(
                        1L,
                        "https://example.com/user123.jpg"
                ),
                new TagPicResponse(
                        2L,
                        "https://example.com/user456.jpg"
                )
        );
        String pageToken = "page1";
        Boolean hasNext = true;

        PageInfo<TagPicResponse> pageInfo = PageInfo.of(pageToken, expectedPosts, hasNext);

        given(postService.getMyTagAlbums(any(), any(), any()))
                .willReturn(pageInfo);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/tag/{tagId}/me", 1L)
                        .param("pageToken", "page1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<TagPicResponse>>() {}
        );

        Assertions.assertThat(response.data()).isEqualTo(expectedPosts);
        Assertions.assertThat(response.pageToken()).isEqualTo(pageToken);
        Assertions.assertThat(response.hasNext()).isEqualTo(hasNext);
    }

    @Test
    @DisplayName("Tag 별 다른 유저의 게시물들을 조회합니다.")
    void test_get_other_members_tag_posts() throws Exception {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        List<String> postImages = Arrays.asList("image1", "image2", "image3");

        var expectedPosts = List.of(
                new PostDetailResponse(
                        1L,
                        LocalDateTime.now(),
                        1001L,
                        "https://example.com/user123.jpg",
                        "user123",
                        "Test Title 1",
                        "Test content for post 1",
                        MealTime.LUNCH,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        2,
                        10,
                        true,
                        true
                ),
                new PostDetailResponse(
                        2L,
                        LocalDateTime.now(),
                        1002L,
                        "https://example.com/user456.jpg",
                        "user456",
                        "Test Title 2",
                        "Test content for post 2",
                        MealTime.DINNER,
                        FoodTag.ASIAN,
                        postImages,
                        tags,
                        3,
                        5,
                        false,
                        false
                )
        );

        String pageToken = "page1";
        Boolean hasNext = true;

        PageInfo<PostDetailResponse> pageInfo = PageInfo.of(pageToken, expectedPosts, hasNext);

        given(postService.getOthersTagAlbums(any(), any(), any()))
                .willReturn(pageInfo);

        MvcResult result = mockMvc.perform(get("/api/v1/posts/tag/{tagId}", 1L)
                        .param("pageToken", "page1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostDetailResponse>>() {}
        );

        Assertions.assertThat(response.data()).isEqualTo(expectedPosts);
        Assertions.assertThat(response.pageToken()).isEqualTo(pageToken);
        Assertions.assertThat(response.hasNext()).isEqualTo(hasNext);
    }
}