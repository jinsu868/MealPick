package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.dto.response.TagResponse;
import com.all_i.allibe.post.dto.response.TagSearchResponse;

import java.util.List;

public interface TagHistoryRepositoryCustom {
    List<TagResponse> getTagCounts();
    List<TagSearchResponse> searchTags(String keyword);
}
