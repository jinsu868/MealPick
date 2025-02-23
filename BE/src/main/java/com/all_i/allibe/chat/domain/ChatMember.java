package com.all_i.allibe.chat.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "display_idx")
    private Long displayIdx;

    public static ChatMember createChatMember(
            Long memberId,
            Long chatRoomId,
            Long displayIdx
    ) {
        return new ChatMember(
                memberId,
                chatRoomId,
                displayIdx
        );
    }

    private ChatMember(
            Long memberId,
            Long chatRoomId,
            Long displayIdx
    ) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
        this.displayIdx = displayIdx;
    }

    public void updateDisplayIdx(Long lastChatId) {
        displayIdx = lastChatId;
    }
}
