package com.all_i.allibe.scrab.domain.repository;

import com.all_i.allibe.scrab.dto.ScrabInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrabRepositoryCustom {
    List<ScrabInfo> getMyScrabs(Long userId);
}
