package com.all_i.allibe.scrab.application;

import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.domain.repository.PostRepository;
import com.all_i.allibe.scrab.domain.Scrab;
import com.all_i.allibe.scrab.domain.repository.ScrabRepository;
import com.all_i.allibe.scrab.dto.ScrabInfo;
import com.all_i.allibe.scrab.dto.response.ScrabResponse;
import com.all_i.allibe.scrab.domain.repository.ScrabRepositoryCustomImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrabService {

    private final ScrabRepository scrabRepository;
    private final ScrabRepositoryCustomImpl scrabRepositoryImpl;
    private final PostRepository postRepository;

    public Long createScrab(
            Long postId,
            Member member
    ) {

        if (!postRepository.existsById(postId)) {
            throw new BadRequestException(ErrorCode.POST_NOT_FOUND);
        }

        if (scrabRepository.existsByPostId(postId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_SCRAB);
        }

        Scrab scrab = Scrab.createScrab(
                member.getId(),
                postId
        );

        Long scrabId = scrabRepository.save(scrab).getId();

        return scrabId;
    }

    public List<ScrabResponse> getScrabs(Member loginMember) {

        Long loginId = loginMember.getId();
        List<ScrabInfo> scrabInfos = scrabRepositoryImpl.getMyScrabs(loginId);
        List<ScrabResponse> response = new ArrayList<>();
        for (ScrabInfo scrabInfo : scrabInfos) {
            ScrabResponse newResponse = ScrabResponse.from(scrabInfo);
            response.add(newResponse);
        }

        return response;
    }

    public void deleteScrab(
            Long postId
    ) {

        Scrab scrab = scrabRepository.findByPostId(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.SCRAB_NOT_FOUND));

        scrabRepository.delete(scrab);
    }
}
