package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.dto.CommentLikeCount;
import java.util.List;

public interface CommentLikeRepositoryCustom {
    List<CommentLikeCount> getCommentLikeCounts(List<Long> commentIds);
}
