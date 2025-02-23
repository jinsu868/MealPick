package com.all_i.allibe.member.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.fcm.dto.FcmTokenUpdateRequest;
import com.all_i.allibe.member.application.MemberService;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.dto.MemberResponse;
import com.all_i.allibe.member.dto.request.MemberAliasCreateRequest;
import com.all_i.allibe.member.dto.request.MemberLocationUpdateRequest;
import com.all_i.allibe.member.dto.request.NoneSocialMemberUpdateRequest;
import com.all_i.allibe.member.dto.request.SocialMemberUpdateRequest;
import com.all_i.allibe.member.dto.response.MemberDetailResponse;
import com.all_i.allibe.member.dto.response.MemberSearchResponse;
import com.all_i.allibe.s3.application.S3FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final S3FileService s3FileService;

    @GetMapping("/me")
    public ResponseEntity<MemberDetailResponse> getMe(@AuthMember Member member) {
        return ResponseEntity.ok(memberService.findMe(member));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> findMember(
            @AuthMember Member member,
            @PathVariable(name = "memberId") Long memberId
    ){
        MemberResponse response = memberService.findMember(memberId);
        return ResponseEntity.ok().body(response);
    }

    /**
     *
     * @param profileImage : 유저 프로필 이미지
     * @param socialMemberUpdateRequest : 소셜 로그인 유저 정보 수정 데이터
     */
    @PatchMapping(
            value = "/social",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateSocialMember(
            @AuthMember Member member,
            @RequestPart SocialMemberUpdateRequest socialMemberUpdateRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        String imagePath = null;
        if(profileImage != null){
            imagePath = uploadProfileImage(profileImage);
        }

        memberService.updateSocialMember(
                member,
                socialMemberUpdateRequest,
                imagePath
        );
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param noneSocialMemberUpdateRequest : 일반 유저 정보 수정 데이터
     * @param profileImage : 유저 프로필 이미지
     */
    @PatchMapping("/none-social")
    public ResponseEntity<Void> updateNoneSocialMember(
            @AuthMember Member member,
            @RequestPart NoneSocialMemberUpdateRequest noneSocialMemberUpdateRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        String imagePath = null;
        if(profileImage != null){
            imagePath = uploadProfileImage(profileImage);
        }

        memberService.updateNoneSocialMember(
                member,
                noneSocialMemberUpdateRequest,
                imagePath
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthMember Member member) {
        memberService.deleteMember(member);
        return ResponseEntity.noContent().build();
    }

    private String uploadProfileImage(MultipartFile profileImage){
        String dirName = "member";
        return s3FileService.uploadFile(profileImage, dirName);
    }

    @PutMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @AuthMember Member member,
            @RequestBody @Valid FcmTokenUpdateRequest request
    ) {
        memberService.updateFcmToken(
                member.getId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> saveFcmToken(
            @AuthMember Member member,
            @RequestBody @Valid FcmTokenUpdateRequest request
    ){
        Long memberId = memberService.saveFcmToken(
                member.getId(),
                request
        );
        return ResponseEntity.created(URI.create("/api/v1/members/" + memberId + "/fcm-token"))
                .build();
    }

    @PostMapping("/alias")
    public ResponseEntity<Void> createAlias(
            @RequestBody MemberAliasCreateRequest memberAliasCreateRequest,
            @AuthMember Member member
    ) {
        memberService.createAlias(
                memberAliasCreateRequest,
                member
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @AuthMember Member member,
            @RequestBody MemberLocationUpdateRequest memberLocationUpdateRequest
    ) {
        memberService.updateLocation(member, memberLocationUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<MemberSearchResponse>> searchMembers(
            @AuthMember Member member,
            @PathVariable(name = "keyword") String keyword
    ){
        List<MemberSearchResponse> members = memberService.searchMembers(
                member,
                keyword
        );

        return ResponseEntity.ok().body(members);
    }
}
