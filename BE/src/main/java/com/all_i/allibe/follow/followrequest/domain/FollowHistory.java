package com.all_i.allibe.follow.followrequest.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus.STAND_BY;

@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"requester_id", "recipient_id"})
})
public class FollowHistory extends BaseEntity {

    @Id
    @Column(name = "follow_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id",nullable = false)
    private Long requesterId;

    @Column(name = "recipient_id",nullable = false)
    private Long recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "request_status")
    private FollowRequestStatus followRequestStatus = STAND_BY;

    @Getter
    public enum FollowRequestStatus {
        ACCEPT("수락"),
        REFUSE("거절"),
        STAND_BY("대기"),
        WITHDRAW("철회")
        ;
        private String followStatus;
        FollowRequestStatus(String followStatus){
            this.followStatus = followStatus;
        }
    }

    public static FollowHistory createRequest(
            Long requesterId,
            Long recipientId
    ){
        return new FollowHistory(
                requesterId,
                recipientId
        );
    }

    private FollowHistory(
            Long requesterId,
            Long recipientId
    ){
        this.requesterId = requesterId;
        this.recipientId = recipientId;
    }

    public void updateStatus(FollowRequestStatus followRequestStatus){
        this.followRequestStatus = followRequestStatus;
    }

    public boolean isRequesterId(Long requesterId){
        return this.requesterId.equals(requesterId);
    }

    public boolean isRecipientId(Long recipientId){
        return this.recipientId.equals(recipientId);
    }
}