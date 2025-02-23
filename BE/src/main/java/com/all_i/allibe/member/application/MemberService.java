package com.all_i.allibe.member.application;

import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.common.exception.NotFoundException;
import com.all_i.allibe.fcm.dto.FcmTokenUpdateRequest;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import com.all_i.allibe.member.domain.repository.MemberRepositoryCustomImpl;
import com.all_i.allibe.member.dto.MemberResponse;
import com.all_i.allibe.member.dto.request.MemberAliasCreateRequest;
import com.all_i.allibe.member.dto.request.MemberLocationUpdateRequest;
import com.all_i.allibe.member.dto.request.NoneSocialMemberUpdateRequest;
import com.all_i.allibe.member.dto.request.SocialMemberUpdateRequest;
import com.all_i.allibe.member.dto.response.MemberDetailResponse;
import com.all_i.allibe.member.dto.response.MemberSearchResponse;
import com.all_i.allibe.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.all_i.allibe.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String ALIAS_FORM = "%s %s [%s]";

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberRepositoryCustomImpl memberRepositoryCustomImpl;

    public MemberResponse findMember(Long memberId) {
        Member member = getMember(memberId);
        return MemberResponse.from(member);
    }

    public MemberDetailResponse findMe(Member member) {
        return MemberDetailResponse.from(member);
    }

    @Transactional
    public void updateSocialMember(
            Member loginMember,
            SocialMemberUpdateRequest socialMemberUpdateRequest,
            String imagePath
    ) {
        if (!loginMember.isSocial()) {
            throw new BadRequestException(NOT_SOCIAL_MEMBER);
        }

        Member member = getMember(loginMember.getId());

        if (imagePath != null) {
            member.updateSocial(
                    socialMemberUpdateRequest.nickname(),
                    socialMemberUpdateRequest.darkModeCheck(),
                    socialMemberUpdateRequest.noticeCheck(),
                    imagePath
            );
        } else {
            member.updateSocial(
                    socialMemberUpdateRequest.nickname(),
                    socialMemberUpdateRequest.darkModeCheck(),
                    socialMemberUpdateRequest.noticeCheck()
            );
        }
    }

    @Transactional
    public void updateNoneSocialMember(
            Member loginMember,
            NoneSocialMemberUpdateRequest noneSocialMemberUpdateRequest,
            String imagePath
    ) {
        if (loginMember.isSocial()) {
            throw new BadRequestException(NOT_NONE_SOCIAL_MEMBER);
        }

        Member member = getMember(loginMember.getId());
        // TODO: password encrypt
        member.updateNoneSocial(
                noneSocialMemberUpdateRequest.nickname(),
                noneSocialMemberUpdateRequest.description(),
                noneSocialMemberUpdateRequest.darkModeCheck(),
                noneSocialMemberUpdateRequest.noticeCheck(),
                noneSocialMemberUpdateRequest.password(),
                imagePath
        );
    }

    public void updateFcmToken(
            Long memberId,
            FcmTokenUpdateRequest request
    ) {
        request.validate();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateFcmToken(request.fcmToken());
    }

    public List<String> getFcmTokensByMemberIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .map(Member::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .toList();
    }

    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    @Transactional
    public void createAlias(
            MemberAliasCreateRequest memberAliasCreateRequest,
            Member loginMember
    ) {
        Member member = getMember(loginMember.getId());
        var topTag = postRepository.findTopTagByMemberId(loginMember.getId())
                .orElseThrow(() -> new BadRequestException(AT_LEAST_ONE_POST_REQUIRED));
        var mostFrequentMealTime = postRepository.findMostFrequentEatingTimeByFoodTag(
                member.getId(),
                topTag.foodTag()
        ).orElseThrow(() -> new BadRequestException(AT_LEAST_ONE_POST_REQUIRED));


        member.updateAlias(String.format(
                ALIAS_FORM,
                mostFrequentMealTime.mealTime().getKey(),
                topTag.foodTag().getKey(),
                memberAliasCreateRequest.alias()
        ));
    }

    public Long saveFcmToken(Long id, FcmTokenUpdateRequest request) {
        Member member = getMember(id);
        member.updateFcmToken(request.fcmToken());
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public void updateLocation(
            Member loginMember,
            MemberLocationUpdateRequest memberLocationUpdateRequest
    ) {
        Member member = getMember(loginMember.getId());
        member.updateLocation(
                memberLocationUpdateRequest.latitude(),
                memberLocationUpdateRequest.longitude()
        );
    }

    public List<MemberSearchResponse> searchMembers(
            Member loginMember,
            String keyword
    ) {
        return memberRepositoryCustomImpl.searchMembers(
                loginMember.getId(),
                keyword
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(()-> new NotFoundException(
                ErrorCode.MEMBER_NOT_FOUND) {
        });
    }
}
