package com.all_i.allibe.scrab.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.scrab.application.ScrabService;
import com.all_i.allibe.scrab.dto.response.ScrabResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ScrabController {

    private final ScrabService scrabService;

    @PostMapping("/posts/{postId}/scrab")
    public ResponseEntity<Void> createScrab(
            @AuthMember Member member,
            @PathVariable("postId") Long postId
    ) {
        Long scrabId = scrabService.createScrab(
                postId,
                member
        );
        return ResponseEntity.created(URI.create("/api/v1/scrabs/" + scrabId))
                .build();
    }

    @GetMapping("/scrabs")
    public ResponseEntity<List<ScrabResponse>> getAllScrabs(
            @AuthMember Member member
    ) {
        List<ScrabResponse> scrabs = scrabService.getScrabs(member);

        return ResponseEntity.ok().body(scrabs);
    }


    @DeleteMapping("/scrabs/{postId}")
    public ResponseEntity<Void> deleteScrab(
            @AuthMember Member member,
            @PathVariable(name = "postId") Long postId
    ) {
        scrabService.deleteScrab(postId);
        return ResponseEntity.noContent().build();
    }
}
