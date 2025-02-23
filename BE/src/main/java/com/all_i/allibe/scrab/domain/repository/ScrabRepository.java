package com.all_i.allibe.scrab.domain.repository;

import com.all_i.allibe.scrab.domain.Scrab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrabRepository extends JpaRepository<Scrab, Long>, ScrabRepositoryCustom {

    List<Scrab> findAllScrabsByMemberId(Long memberId);

    Optional<Scrab> findByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    boolean existsByPostId(Long postId);
}
