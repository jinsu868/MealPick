package com.all_i.allibe.follow.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.follow.application.FollowService;
import com.all_i.allibe.follow.dto.FollowMemberInfo;
import com.all_i.allibe.follow.dto.FollowStatusResponse;
import com.all_i.allibe.follow.dto.FollowingMemberInfo;
import com.all_i.allibe.follow.dto.followrequest.FollowRequest;
import com.all_i.allibe.follow.dto.followrequest.FollowRequestResponse;
import com.all_i.allibe.follow.dto.followrequest.FollowStatusRequest;
import com.all_i.allibe.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/request")
    public ResponseEntity<FollowRequestResponse> requestFollow(
        @Valid @RequestBody FollowRequest request,
        @AuthMember Member member
    ) {
        return ResponseEntity.ok(
            followService.requestFollow(
                    member.getId(),
                    request.recipientId()
            )
        );
    }

    @PatchMapping("/request/handle")
    public ResponseEntity<FollowRequestResponse> handleFollow(
        @RequestBody FollowStatusRequest request,
        @AuthMember Member member
    ) {
        return ResponseEntity.ok(
            followService.handleFollow(
                    request.requesterId(),
                    request.status(),
                    member
            )
        );
    }

    @GetMapping("/me/follower")
    public ResponseEntity<PageInfo<FollowMemberInfo>> fetchFollowers(
            @AuthMember Member member,
            @RequestParam(value = "pageToken", required = false) String pageToken
    ){
        return ResponseEntity.ok(
                followService.fetchFollowers(
                        member.getId(),
                        pageToken
                )
        );
    }

    @GetMapping("/me/following")
    public ResponseEntity<PageInfo<FollowingMemberInfo>> fetchFollows(
            @AuthMember Member member,
            @RequestParam(value = "pageToken", required = false) String pageToken
    ){
        return ResponseEntity.ok(
                followService.fetchFollows(
                        member.getId(),
                        pageToken
                )
        );
    }

    @GetMapping("/stand-by")
    public ResponseEntity<PageInfo<FollowMemberInfo>> fetchFollowRequest(
            @AuthMember Member member,
            @RequestParam(value = "pageToken", required = false) String pageToken
    ){
        return ResponseEntity.ok(
                followService.fetchFollowRequest(
                        member.getId(),
                        pageToken
                )
        );
    }

    @DeleteMapping("{recipientId}")
    public ResponseEntity<Void> unfollow(
            @AuthMember Member member,
            @PathVariable Long recipientId
    ) {
        followService.unFollow(
                member.getId(),
                recipientId
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{someoneId}")
    public ResponseEntity<FollowStatusResponse> getFollowStatus(
            @AuthMember Member member,
            @PathVariable Long someoneId
    ){
        FollowStatusResponse response = followService.findFollowStatus(
                member.getId(),
                someoneId
        );
        return ResponseEntity.ok(response);
    }
}
