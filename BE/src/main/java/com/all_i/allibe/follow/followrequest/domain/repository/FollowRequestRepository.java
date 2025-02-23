package com.all_i.allibe.follow.followrequest.domain.repository;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowHistory, Long>, FollowRequestRepositoryCustom {

    Optional<List<FollowHistory>> findByRequesterIdAndRecipientId(
            Long requesterId,
            Long recipientId
    );

    List<FollowHistory> findAllByRecipientId(Long id);

}
