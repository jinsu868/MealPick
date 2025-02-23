package com.all_i.allibe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "social_id"
            }
        )
    }
)
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE member_id = ?")
@SQLRestriction("deleted = false")
public class Member {

    private static final int MAX_SOCIAL_ID_LENGTH = 255;
    //기존 좌표 값이 존재 한다면 해당 값 과의 차이 계수
    private static final BigDecimal COORDINATE_THRESHOLD = new BigDecimal("0.0001");


    @Id
    @Column(nullable = false, name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Size(max = MAX_SOCIAL_ID_LENGTH)
    @Column(name = "social_id")
    private String socialId;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "notice_check")
    private Boolean noticeCheck;

    @Column(name = "dark_mode_check")
    private Boolean darkModeCheck;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "last_notification_time")
    private LocalDateTime lastNotificationTime;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "alias")
    private String alias;

    public static Member of(
            String socialId,
            String nickname,
            String profileImageUrl
    ) {
        return new Member(
                socialId,
                nickname,
                profileImageUrl
        );
    }

    public static Member of(
            Long id,
            String socialId,
            String nickname,
            String profileImage
    ) {
        return new Member(
                id,
                socialId,
                nickname,
                profileImage
        );
    }

    private Member(
            Long id,
            String socialId,
            String nickname,
            String profileImage
    ) {
        this.id = id;
        this.socialId = socialId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    private Member(
            String socialId,
            String nickname,
            String profileImage
    ) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.deleted = false;
    }

    public void updateSocial(
            String nickname,
            boolean darkModeCheck,
            boolean noticeCheck,
            String profileImage
    ) {
        this.nickname = nickname;
        this.darkModeCheck = darkModeCheck;
        this.noticeCheck = noticeCheck;
        this.profileImage = profileImage;
    }

    public void updateSocial(
            String nickname,
            boolean darkModeCheck,
            boolean noticeCheck
    ) {
        this.nickname = nickname;
        this.darkModeCheck = darkModeCheck;
        this.noticeCheck = noticeCheck;
    }

    public boolean isSocial() {
        return socialId != null;
    }

    public void updateNoneSocial(
        String nickname,
        String description,
        boolean darkModeCheck,
        boolean noticeCheck,
        String password,
        String imagePath
    ) {
        this.nickname = nickname;
        this.description = description;
        this.darkModeCheck = darkModeCheck;
        this.noticeCheck = noticeCheck;
        this.profileImage = imagePath;
    }

    public void updateLocation(
        Double latitude,
        Double longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String updateName(String name){
        return this.name = name;
    }

    public String updateFcmToken(String fcmToken){
        return this.fcmToken = fcmToken;
    }

    public void signUp(
            int age,
            String name,
            String email
    ) {
        this.email = email;
        this.name = name;
        this.email = email;
    }

    public void updateAlias(String alias) {
        this.alias = alias;
    }
}
