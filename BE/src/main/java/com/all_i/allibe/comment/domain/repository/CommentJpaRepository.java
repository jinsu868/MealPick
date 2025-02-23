package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long>, CommentJpaRepositoryCustom {
}
