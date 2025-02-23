package com.all_i.allibe.chat.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import com.all_i.allibe.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {

    private static final String CHAT_ROOM_ENTER_MESSAGE = "%s님이 채팅방에 입장하셨습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    public static Chat createChat(
            Long memberId,
            String content,
            Long chatRoomId
    ) {
        return new Chat(
                memberId,
                content,
                chatRoomId
        );
    }

    public static Chat createChat(
            Member member,
            Long chatRoomId
    ) {
        return new Chat(
                member,
                chatRoomId
        );
    }

    private Chat(
            Member member,
            Long chatRoomId
    ) {
        this.memberId = member.getId();
        this.chatRoomId = chatRoomId;
        this.content = String.format(CHAT_ROOM_ENTER_MESSAGE, member.getName());
    }

    private Chat(
            Long memberId,
            String content,
            Long chatRoomId
    ) {
        this.memberId = memberId;
        this.content = content;
        this.chatRoomId = chatRoomId;
    }
}
