package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long>, ChatRoomJpaRepositoryCustom {
}
