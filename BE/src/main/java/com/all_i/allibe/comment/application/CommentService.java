package com.all_i.allibe.comment.application;

import com.all_i.allibe.comment.domain.Comment;
import com.all_i.allibe.comment.domain.CommentLike;
import com.all_i.allibe.comment.domain.repository.CommentLikeRepository;
import com.all_i.allibe.comment.domain.repository.CommentRepository;
import com.all_i.allibe.comment.dto.CommentInfo;
import com.all_i.allibe.comment.dto.CommentLikeCount;
import com.all_i.allibe.comment.dto.request.ChildCommentCreateRequest;
import com.all_i.allibe.comment.dto.request.CommentUpdateRequest;
import com.all_i.allibe.comment.dto.request.RootCommentCreateRequest;
import com.all_i.allibe.comment.dto.response.CommentResponse;
import com.all_i.allibe.comment.dto.response.PostCommentResponse;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import com.all_i.allibe.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int COMMENT_PAGE_SIZE = 20;


    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentLikeRepository commentLikeRepository;

    public Long createRootComment(
            Long postId,
            RootCommentCreateRequest rootCommentCreateRequest,
            Member loginMember
    ) {
        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        Comment comment = Comment.createComment(
                postId,
                loginMember.getId(),
                rootCommentCreateRequest.content()
        );
        return commentRepository.save(comment).getId();
    }

    public Long createChildComment(
            Long commentId,
            ChildCommentCreateRequest childCommentCreateRequest,
            Member member
    ) {
        Comment rootComment = findComment(commentId);

        if (!rootComment.isRoot()) {
            throw new BadRequestException(ErrorCode.NO_MORE_COMMENT);
        }

        Comment childComment = Comment.createComment(
                rootComment.getPostId(),
                member.getId(),
                rootComment.getId(),
                childCommentCreateRequest.content()
        );

        return commentRepository.save(childComment).getId();
    }

    public void deleteComment(
            Long commentId,
            Member member
    ) {
        Comment comment = findComment(commentId);
        comment.validateOwner(member);

        commentRepository.delete(comment);
    }

    public PageInfo<PostCommentResponse> findAllRootComment(
            Long postId,
            Member loginMember,
            String pageToken
    ) {
        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        List<CommentResponse> comments = commentRepository.findAllRootCommentByPostId(postId, pageToken);
        List<PostCommentResponse> commentResponses = new ArrayList<>();
        List<Long> likedIds = commentLikeRepository.findAllCommentIdByMemberId(loginMember.getId())
                .stream()
                .map(CommentLike::getCommentId)
                .toList();

        for (CommentResponse comment : comments) {
            boolean isLiked = false;

            Member author = memberRepository.findById(comment.authorId())
                    .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_FOUND));

            if (likedIds.contains(comment.id())) {
                isLiked = true;
            }

            int childCount = commentRepository.countByParentCommentId(comment.id());

            PostCommentResponse postCommentResponse = new PostCommentResponse(
                    comment.id(),
                    comment.authorId(),
                    author.getNickname(),
                    author.getProfileImage(),
                    comment.content(),
                    isLiked,
                    comment.deleted(),
                    childCount,
                    comment.likeCount(),
                    comment.createdAt()
            );

            commentResponses.add(postCommentResponse);
        }

        var data = commentResponses;

        if (data.size() <= COMMENT_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.id());

        return PageInfo.of(nextPageToken, data, true);
    }

    public PageInfo<PostCommentResponse> findAllChildComment(
            Long parentCommentId,
            Member loginMember,
            String pageToken
    ) {
        Comment comment = findComment(parentCommentId);
        if (!comment.isRoot()) {
            throw new BadRequestException(ErrorCode.NO_MORE_COMMENT);
        }

        Long loginId = loginMember.getId();

        PageInfo<CommentInfo> commentInfos = commentRepository.findAllChildCommentAndMemberByParentCommentId(parentCommentId, pageToken);

        List<Long> commentIds = commentInfos.data().stream()
                .map(CommentInfo::id)
                .collect(Collectors.toList());
        List<Long> commentLikes = commentLikeRepository.findAllCommentIdByMemberId(loginMember.getId())
                .stream()
                .map(CommentLike::getId)
                .toList();
        Map<Long, Long> commentLikeCounts = commentLikeRepository.getCommentLikeCounts(commentIds)
                .stream()
                .collect(Collectors.toMap(CommentLikeCount::commentId, CommentLikeCount::likeCount));

        List<PostCommentResponse> responses = new ArrayList<>();

        for (CommentInfo response : commentInfos.data()) {
            PostCommentResponse postCommentResponse = new PostCommentResponse(
                    response.id(),
                    response.authorId(),
                    response.nickName(),
                    response.profileImageUrl(),
                    response.content(),
                    commentLikes.contains(response.id()),
                    response.deleted(),
                    0,
                    commentLikeCounts.getOrDefault(response.id(), 0L).intValue(),
                    response.createdAt()
            );

            responses.add(postCommentResponse);
        }

        return PageInfo.of(commentInfos.pageToken(), responses, commentInfos.hasNext());
    }

    @Transactional
    public void updateComment(
            Long commentId,
            Member member,
            CommentUpdateRequest commentUpdateRequest
    ) {
        Comment comment = findComment(commentId);
        comment.validateOwner(member);

        comment.update(commentUpdateRequest.content());
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
