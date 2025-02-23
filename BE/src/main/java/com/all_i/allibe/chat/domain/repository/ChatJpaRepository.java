package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatJpaRepository extends JpaRepository<Chat, Long>, ChatJpaRepositoryCustom {
}
