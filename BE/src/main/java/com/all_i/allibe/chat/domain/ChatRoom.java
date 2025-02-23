package com.all_i.allibe.chat.domain;

import com.all_i.allibe.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    String name;

    public static ChatRoom from(String name) {
        return new ChatRoom(name);
    }

    private ChatRoom(String name) {
        this.name = name;
    }
}
