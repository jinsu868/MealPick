package com.all_i.allibe.follow.domain.repository;

import com.all_i.allibe.follow.domain.Follow;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long>, FollowRepositoryCustom {


    Boolean existsByFollowerIdAndFollowingId(
            Long requesterId,
            Long recipientId
    );

    void deleteByFollowerIdAndFollowingId(
            Long memberId,
            Long recipientId
    );
}
