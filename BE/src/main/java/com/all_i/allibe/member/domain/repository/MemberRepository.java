package com.all_i.allibe.member.domain.repository;

import com.all_i.allibe.member.domain.Member;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    Optional<Member> findBySocialId(String socialId);

    @Modifying
    @Query("UPDATE Member m SET m.lastNotificationTime = null")
    void clearAllMembersLastNotificationTime();
}
