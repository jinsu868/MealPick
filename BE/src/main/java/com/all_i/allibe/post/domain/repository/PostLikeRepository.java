package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryCustom {
    boolean existsByMemberIdAndPostId(
            Long memberId,
            Long postId
    );

    Optional<PostLike> findByMemberIdAndPostId(
            Long memberId,
            Long postId
    );

    List<PostLike> findAllByMemberIdAndPostIdIn(
            Long memberId,
            List<Long> postId
    );


}
