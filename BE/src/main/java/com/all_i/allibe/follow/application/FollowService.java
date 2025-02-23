package com.all_i.allibe.follow.application;

import com.all_i.allibe.common.aop.ExecuteTime;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.follow.domain.Follow;
import com.all_i.allibe.follow.domain.repository.FollowRepository;
import com.all_i.allibe.follow.domain.repository.FollowRepositoryCustomImpl;
import com.all_i.allibe.follow.dto.FollowInfo;
import com.all_i.allibe.follow.dto.FollowMemberInfo;
import com.all_i.allibe.follow.dto.FollowStatusResponse;
import com.all_i.allibe.follow.dto.FollowingMemberInfo;
import com.all_i.allibe.follow.dto.followrequest.FollowRequestResponse;
import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import com.all_i.allibe.follow.followrequest.domain.repository.FollowRequestQueryRepository;
import com.all_i.allibe.follow.followrequest.domain.repository.FollowRequestRepository;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus;
import static com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus.*;
import static com.all_i.allibe.follow.followrequest.domain.FollowHistory.createRequest;

@Service
@RequiredArgsConstructor
public class FollowService {
    private static final int DEFAULT_SIZE = 10;

    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final FollowRepositoryCustomImpl followQueryRepository;
    private final FollowRequestQueryRepository followRequestQueryRepository;

    @Transactional
    public FollowRequestResponse requestFollow(
            Long requesterId,
            Long recipientId
    ) {
        if(followRepository.existsByFollowerIdAndFollowingId
            (
                requesterId,
                recipientId
            )
        ){
            throw new BadRequestException(ErrorCode.ALREADY_FOLLOWED);
        }

        if (requesterId.equals(recipientId)) {
            throw new BadRequestException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }
        List<FollowHistory> existRequests = followRequestRepository.findByRequesterIdAndRecipientId(
                requesterId,
                recipientId
        ).orElseThrow(()-> new BadRequestException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED));

        if(!existRequests.isEmpty() && !(existRequests.get(existRequests.size()-1).getFollowRequestStatus().equals(STAND_BY)||existRequests.get(existRequests.size()-1).getFollowRequestStatus().equals(WITHDRAW))){
            throw new BadRequestException(ErrorCode.ALREADY_REQUESTED);
        }

        return FollowRequestResponse.from(
                followRequestRepository.save(
                        createRequest(
                                requesterId,
                                getRecipient(recipientId).getId()
                        )
                )
        );
    }

    @Transactional
    public FollowRequestResponse handleFollow(
            Long requesterId,
            FollowRequestStatus status,
            Member member
    ) {
        List<FollowHistory> memberRequests = getFollowRequests(member);

        FollowHistory memberRequest = memberRequests.stream()
                .filter(request -> request.getRecipientId().equals(member.getId())
                        && request.getRequesterId().equals(requesterId))
                .max(Comparator.comparing(FollowHistory::getCreatedAt))
                .orElseThrow(() -> new BadRequestException(ErrorCode.FOLLOW_REQUEST_NOT_FOUND));
        if(memberRequest.getFollowRequestStatus().equals(ACCEPT)){
            throw new BadRequestException(ErrorCode.ALREADY_FOLLOWED);
        }
        validateAuthority(
                memberRequest,
                status,
                member
        );
        memberRequest.updateStatus(status);
        if (status.equals(ACCEPT)){
            followRepository.save(Follow.create(
                    memberRequest.getRecipientId(),
                    memberRequest.getRequesterId()
            ));
        }

        return FollowRequestResponse.from(memberRequest);
    }

    private void validateAuthority(
            FollowHistory followRequest,
            FollowRequestStatus status,
            Member member
    ) {
        if(status.equals(WITHDRAW)) {
            if(!followRequest.isRequesterId(member.getId())) {
                throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }
        if(status.equals(ACCEPT)||status.equals(REFUSE)) {
            if(!followRequest.isRecipientId(followRequest.getRecipientId())){
                throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }
    }

    public PageInfo<FollowMemberInfo> fetchFollowRequest(
            Long memberId,
            String pageToken
    ) {
        List<FollowHistory> requests = followRequestQueryRepository.findFollowRequests(
                memberId,
                pageToken,
                DEFAULT_SIZE + 1
        );

        // 요청자 ID들을 한 번에 조회
        List<Long> requesterIds = requests.stream()
                .limit(DEFAULT_SIZE)
                .map(FollowHistory::getRequesterId)
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(requesterIds)
                .stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        member -> member
                ));

        List<FollowMemberInfo> followRequests = requests.stream()
                .limit(DEFAULT_SIZE)
                .map(request -> {
                    Member requester = memberMap.get(request.getRequesterId());
                    if (requester == null) {
                        throw new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                    }
                    return FollowMemberInfo.of(
                            requester.getId(),
                            requester.getProfileImage(),
                            requester.getNickname()
                    );
                })
                .toList();

        boolean hasNext = requests.size() > DEFAULT_SIZE;  // requests.size()로 체크해야 함
        String nextPageToken = hasNext ?
                requests.get(DEFAULT_SIZE - 1).getId().toString() :
                null;

        return PageInfo.of(
                nextPageToken,
                followRequests,
                hasNext
        );
    }

    @ExecuteTime
    public PageInfo<FollowMemberInfo> fetchFollowers(
            Long memberId,
            String pageToken
    ) {
        List<Follow> followers = followQueryRepository.findFollowers(
                memberId,
                pageToken,
                DEFAULT_SIZE + 1
        );

        List<Long> followerIds = followers.stream()
                .limit(DEFAULT_SIZE)
                .map(Follow::getFollowerId)
                .toList();
                ;

        Map<Long,Member> memberMap = memberRepository.findAllById(followerIds)
                .stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        member->member
                ));

        List<FollowMemberInfo> memberInfoList = followers.stream()
                .limit(DEFAULT_SIZE)
                .map(follow -> {
                    Member member = memberMap.get(follow.getFollowerId());
                    if (member == null) {
                        throw new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                    }
                    return FollowMemberInfo.of(
                            member.getId(),
                            member.getProfileImage(),
                            member.getNickname()
                    );
                })
                .toList();

        boolean hasNext = followers.size() > DEFAULT_SIZE;
        String nextPageToken = hasNext ? followers.get(followers.size() - 1).getId().toString() :
                null;

        return PageInfo.of(
                nextPageToken,
                memberInfoList,
                hasNext
        );
    }

    public PageInfo<FollowingMemberInfo> fetchFollows(
            Long memberId,
            String pageToken
    ) {
        List<Follow> follows = followQueryRepository.findFollows(
                memberId,
                pageToken,
                DEFAULT_SIZE + 1
        );

        // 팔로잉하는 멤버 ID들을 한 번에 조회
        List<Long> followingIds = follows.stream()
                .limit(DEFAULT_SIZE)
                .map(Follow::getFollowingId)
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(followingIds)
                .stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        member -> member
                ));

        List<FollowingMemberInfo> memberInfoList = follows.stream()
                .limit(DEFAULT_SIZE)
                .map(follow -> {
                    Member member = memberMap.get(follow.getFollowingId());
                    if (member == null) {
                        throw new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                    }
                    return FollowingMemberInfo.of(
                            member.getId(),
                            member.getProfileImage(),
                            member.getNickname()
                    );
                })
                .toList();

        boolean hasNext = follows.size() > DEFAULT_SIZE;
        String nextPageToken = hasNext ?
                follows.get(DEFAULT_SIZE - 1).getId().toString() :
                null;

        return PageInfo.of(
                nextPageToken,
                memberInfoList,
                hasNext
        );
    }

    private List<FollowHistory> getFollowRequests(Member member) {
        return followRequestRepository.findAllByRecipientId(member.getId());
    }

    private Member getRecipient(Long recipientId) {
        return memberRepository.findById(recipientId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void unFollow(
            Long memberId,
            Long recipientId
    ) {
        followRepository
                .deleteByFollowerIdAndFollowingId(
                        memberId,
                        recipientId
                );

        FollowHistory lastAcceptedRequest = followRequestRepository
                .findLastAcceptedRequest(memberId,recipientId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.FOLLOW_HISTORY_NOT_FOUND));

        lastAcceptedRequest.updateStatus(WITHDRAW);
        followRequestRepository.save(lastAcceptedRequest);
    }

    public FollowStatusResponse findFollowStatus(
            Long memberId,
            Long someoneId
    ) {
        List<FollowHistory> followHistories = followRequestRepository
                .findByRequesterIdAndRecipientId(memberId, someoneId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_FOUND));
        if(!followHistories.isEmpty()){
            FollowHistory followHistory = followHistories.get(followHistories.size() - 1);
            return FollowStatusResponse.from(followHistory);
        }
        return null;
    }
}
