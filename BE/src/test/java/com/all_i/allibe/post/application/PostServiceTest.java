//package com.all_i.allibe.post.application;
//
//import com.all_i.allibe.common.exception.BadRequestException;
//import com.all_i.allibe.common.exception.ErrorCode;
//import com.all_i.allibe.member.domain.Member;
//import com.all_i.allibe.post.domain.*;
//import com.all_i.allibe.post.domain.repository.PostRepository;
//import com.all_i.allibe.post.domain.repository.PostTagRepository;
//import com.all_i.allibe.post.domain.repository.TagRepository;
//import com.all_i.allibe.post.dto.request.PostCreateRequest;
//import com.all_i.allibe.post.dto.request.PostUpdateRequest;
//import com.all_i.allibe.postimage.domain.repository.PostImageRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//
//import java.lang.reflect.Field;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicLong;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@AutoConfigureRestDocs
//@ExtendWith(MockitoExtension.class)
//class PostServiceTest {
//
//    @InjectMocks
//    private PostService postService;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private TagRepository tagRepository;
//
//    @Mock
//    private PostTagRepository postTagRepository;
//
//    @Mock
//    private PostImageRepository postImageRepository;
//
//    @Test
//    @DisplayName("게시물을 생성한다.")
//    public void can_create_post() throws Exception {
//        // Given
//        Long postId = 1L;
//        Member member = Member.of("123", "test_nickname", "test_profile_image");
//        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
//        List<String> imageUrls = new ArrayList<>(Arrays.asList("image1", "image2", "image3"));
//
//        PostCreateRequest request = new PostCreateRequest(
//                "test title",
//                "test content",
//                MealTime.LUNCH,
//                FoodTag.ASIAN,
//                37.5665,
//                126.9780,
//                tags
//        );
//
//        Post post = Post.of(
//                member.getId(),
//                request.title(),
//                request.content(),
//                request.mealTime(),
//                request.foodTag(),
//                "image1",
//                request.latitude(),
//                request.longitude()
//        );
//
//        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
//            Post savedPost = invocation.getArgument(0);
//            Field idField = Post.class.getDeclaredField("id");
//            idField.setAccessible(true);
//            idField.set(savedPost, postId);
//            return savedPost;
//        });
//
//        AtomicLong tagIdGenerator = new AtomicLong(100L);
//        given(tagRepository.findByTagName(anyString())).willReturn(Optional.empty());
//        given(tagRepository.save(any(Tag.class))).willAnswer(invocation -> {
//            Tag tag = invocation.getArgument(0);
//            Field tagIdField = Tag.class.getDeclaredField("id");
//            tagIdField.setAccessible(true);
//            tagIdField.set(tag, tagIdGenerator.getAndIncrement());
//            return tag;
//        });
//
//        // When
//        Long createdPostId = postService.createPost(request, imageUrls, member);
//
//        // Then
//        assertThat(createdPostId).isEqualTo(postId);
//
//        verify(postRepository).save(any(Post.class));
//        verify(tagRepository, times(3)).findByTagName(anyString());
//        verify(tagRepository, times(3)).save(any(Tag.class));
//        verify(postTagRepository, times(3)).save(any(PostTag.class));
//        verify(postImageRepository).saveAll(anyList());
//    }
//
//
//    @Test
//    @DisplayName("게시물의 작성자는 게시물을 삭제할 수 있다.")
//    public void can_delete_post() throws Exception {
//        // Given
//        Long postId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//
//        Post post = Post.of(
//                member.getId(),
//                "test title",
//                "test content",
//                MealTime.LUNCH,
//                FoodTag.ASIAN,
//                "sadasdsaasd",
//                37.5665,
//                126.9780
//        );
//
//        given(postRepository.findById(postId))
//                .willReturn(Optional.of(post));
//
//        postService.deletePost(postId, member);
//
//        verify(postRepository).findById(postId);
//        verify(postRepository).delete(post);
//        verify(postTagRepository).deleteAllByPostId(postId);
//    }
//
//    @Test
//    @DisplayName("게시물의 작성자가 아니면 게시물을 삭제할 수 없다.")
//    public void can_not_delete_post_when_not_author() {
//        // Given
//        Long postId = 1L;
//
//        Member author = Member.of(1L, "123", "author", "profile");
//        Member nonAuthor = Member.of(2L, "456", "non-author", "profile");
//
//        Post post = Post.of(
//                author.getId(),
//                "test title",
//                "test content",
//                MealTime.LUNCH,
//                FoodTag.ASIAN,
//                "sadasdas",
//                37.5665,
//                126.9780
//        );
//
//        given(postRepository.findById(postId))
//                .willReturn(Optional.of(post));
//
//        Assertions.assertThatThrownBy(() -> postService.deletePost(postId, nonAuthor))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.NOT_MY_POST.getMessage());
//    }
//
//    @Test
//    @DisplayName("게시물 작성자면 수정할 수 있다")
//    public void updatePost_ByAuthor_SuccessfullyUpdates() {
//        // Given
//        Long postId = 1L;
//        Member author = Member.of(1L, "123", "author", "profile");
//        PostUpdateRequest request = new PostUpdateRequest(
//                "updated_content",
//                List.of("tag1", "tag2")
//        );
//
//        Post post = spy(Post.of(
//                author.getId(),
//                "original_title",
//                "original_content",
//                MealTime.LUNCH,
//                FoodTag.ASIAN,
//                "asdasdas",
//                37.5665,
//                126.9780
//        ));
//
//        given(postRepository.findById(postId))
//                .willReturn(Optional.of(post));
//        given(tagRepository.findByTagName(anyString()))
//                .willReturn(Optional.empty());
//
//        given(tagRepository.save(any(Tag.class)))
//                .willAnswer(invocation -> {
//                    Tag tag = invocation.getArgument(0);
//                    Field idField = Tag.class.getDeclaredField("id");
//                    idField.setAccessible(true);
//                    idField.set(tag, new Random().nextLong());
//                    return tag;
//                });
//
//        postService.updatePost(postId, request, author);
//
//        assertAll(
//                () -> assertThat(post.getContent()).isEqualTo(request.content()),
//                () -> verify(postRepository).findById(postId),
//                () -> verify(post).update(request.content()),
//                () -> verify(postTagRepository).deleteAllByPostId(postId),
//                () -> verify(tagRepository, times(2)).save(any(Tag.class)),
//                () -> verify(postTagRepository, times(2)).save(any(PostTag.class))
//        );
//    }
//
//    @Test
//    @DisplayName("게시물 작성자가 아니면 수정할 수 없다")
//    public void updatePost_ByNonAuthor_ThrowsException() {
//        Long postId = 1L;
//        Member author = Member.of(1L, "123", "author", "profile");
//        Member nonAuthor = Member.of(2L, "456", "non-author", "profile");
//
//        Post post = Post.of(
//                author.getId(),
//                "original_title",
//                "original_content",
//                MealTime.LUNCH,
//                FoodTag.ASIAN,
//                "sadasdas",
//                37.5665,
//                126.9780
//        );
//
//        PostUpdateRequest request = new PostUpdateRequest(
//                "updated_content",
//                List.of("tag1", "tag2")
//        );
//
//        given(postRepository.findById(postId))
//                .willReturn(Optional.of(post));
//
//        assertThatThrownBy(() -> postService.updatePost(postId, request, nonAuthor))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.NOT_MY_POST.getMessage());
//
//        verify(postRepository).findById(postId);
//        verifyNoInteractions(postTagRepository, tagRepository);
//    }
//}