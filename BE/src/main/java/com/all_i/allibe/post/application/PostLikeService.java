package com.all_i.allibe.post.application;

import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.domain.PostLike;
import com.all_i.allibe.post.domain.repository.PostLikeRepository;
import com.all_i.allibe.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public Long createPostLike(
            Long postId,
            Member loginMember
    ) {

        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        if (postLikeRepository.existsByMemberIdAndPostId(loginMember.getId(), postId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_POST_LIKE);
        }

        PostLike postLike = PostLike.createPostLike(postId, loginMember.getId());

        Long postLikeId = postLikeRepository.save(postLike).getId();

        return postLikeId;
    }

    public void deletePostLike(
            Long postId,
            Member loginMember
    ) {
        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        PostLike postLike = findPostLike(loginMember.getId(), postId);
        postLikeRepository.delete(postLike);
    }

    private PostLike findPostLike(Long memberId, Long postId) {
        return postLikeRepository.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_LIKE_NOT_FOUND));
    }
}
