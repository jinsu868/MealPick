package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.domain.TagHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagHistoryRepository extends JpaRepository<TagHistory, Long>, TagHistoryRepositoryCustom {
    boolean existsByTagName(String tagName);
}
