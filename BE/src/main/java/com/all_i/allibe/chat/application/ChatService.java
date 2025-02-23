package com.all_i.allibe.chat.application;

import com.all_i.allibe.chat.domain.Chat;
import com.all_i.allibe.chat.domain.ChatMember;
import com.all_i.allibe.chat.domain.ChatRoom;
import com.all_i.allibe.chat.domain.repository.ChatMemberRepository;
import com.all_i.allibe.chat.domain.repository.ChatRepository;
import com.all_i.allibe.chat.domain.repository.ChatRoomRepository;
import com.all_i.allibe.chat.dto.ChatCreateEvent;
import com.all_i.allibe.chat.dto.request.ChatRequest;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.util.JsonUtil;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.all_i.allibe.common.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int KEY_EXPIRE_TIME = 604800;
    private static final String CHAT_ROOM_KEY = "rooms:%s";
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonUtil jsonUtil;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Write-Around
     */
    @Transactional
    public ChatRelayRequest saveChat(
            ChatRequest chatRequest,
            Long memberId,
            Long roomId
    ) {
        // TODO: Code 정리하기
        Member sender = findMember(memberId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(CHAT_ROOM_NOT_FOUND));

        List<ChatMember> chatMembers = findChatMember(chatRequest.roomId());

        if (!isMemberInChatRoom(chatMembers, memberId)) {
            throw new BadRequestException(MEMBER_NOT_IN_CHAT_ROOM);
        }

        Chat chat = Chat.createChat(
                memberId,
                chatRequest.content(),
                chatRequest.roomId()
        );
        chatRepository.save(chat);

        ChatMember chatPartner = chatMembers.get(0);
        Long beforeDisplayIdx = chatPartner.getDisplayIdx();
        for (ChatMember chatMember : chatMembers) {
            chatMember.updateDisplayIdx(chat.getId());
            if (!sender.getId().equals(chatMember.getMemberId())) {
                chatPartner = chatMember;
            }
        }

        Member partner = findMember(chatPartner.getMemberId());

        var chatResponse = ChatResponse.of(
                chat.getId(),
                sender.getId(),
                roomId,
                sender.getProfileImage(),
                chat.getCreatedAt(),
                chat.getContent()
        );

        var myChatResponse = new ChatRoomResponse(
                roomId,
                chatRoom.getName(),
                chat.getId(),
                partner.getName(),
                chat.getContent(),
                chat.getCreatedAt(),
                partner.getProfileImage()
        );

        var partnerChatResponse = new ChatRoomResponse(
                roomId,
                chatRoom.getName(),
                chat.getId(),
                sender.getName(),
                chat.getContent(),
                chat.getCreatedAt(),
                sender.getProfileImage()
        );

        eventPublisher.publishEvent(
                new ChatCreateEvent(
                        roomId,
                        chatResponse,
                        sender.getId(),
                        chatPartner.getMemberId(),
                        beforeDisplayIdx,
                        myChatResponse,
                        partnerChatResponse
                )
        );

        return ChatRelayRequest.of(
                chat.getId(),
                sender.getId(),
                chatPartner.getMemberId(),
                roomId,
                sender.getProfileImage(),
                chat.getCreatedAt(),
                chat.getContent()
        );
    }

    /**
     * Look Aside
     */
    public PageInfo<ChatResponse> findAll(
            Long chatRoomId,
            Member loginMember,
            String pageToken
    ) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new BadRequestException(CHAT_ROOM_NOT_FOUND);
        }

        if (!chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, loginMember.getId())) {
            throw new BadRequestException(MEMBER_NOT_IN_CHAT_ROOM);
        }

        /**
         * Cache miss
         */
        String chatRoomKey = String.format(CHAT_ROOM_KEY, chatRoomId);
        var zSetOps = redisTemplate.opsForZSet();

        if (!redisTemplate.hasKey(chatRoomKey)) {
            // DB Read & Cache Write
            var chatResponse = chatRepository.findAll(
                    chatRoomId,
                    loginMember.getId(),
                    pageToken,
                    DEFAULT_PAGE_SIZE
            );

            //TODO: Write Lua Script
            chatResponse.data()
                            .forEach(chat -> zSetOps.add(
                                    chatRoomKey,
                                    jsonUtil.convertToJson(chat),
                                    chat.id()
                            ));
            redisTemplate.expire(chatRoomKey, KEY_EXPIRE_TIME, TimeUnit.SECONDS);

            return chatResponse;
        }

        /**
         * 0~20 처음부터 조회하는 경우
         */
        if (pageToken == null) {
            var chatBefore = getChatsBefore(
                    chatRoomKey,
                    Long.MAX_VALUE,
                    DEFAULT_PAGE_SIZE
            );

            // Cache 에 데이터가 다 있는 경우
            if (chatBefore.size() < DEFAULT_PAGE_SIZE) {
                return PageInfo.of(null, chatBefore, false);
            }

            if (chatBefore.size() == DEFAULT_PAGE_SIZE) {
                String nextPageToken = String.valueOf(chatBefore.get(chatBefore.size() - 1).id()-1);
                // Cache 에는 없지만 다음 페이지가 존재하는 경우
                if (chatRepository.existsNextChat(
                        chatRoomId,
                        loginMember.getId(),
                        nextPageToken)
                ) {
                    return PageInfo.of(nextPageToken, chatBefore, true);
                }

                return PageInfo.of(null, chatBefore, false);
            }

            var lastData = chatBefore.get(chatBefore.size() - 1);
            String nextPageToken = String.valueOf(lastData.id());
            chatBefore.remove(chatBefore.size() - 1);
            return PageInfo.of(nextPageToken, chatBefore, true);
        }

        /**
         * pageToken 으로 조회하는 경우
         */
        var chatBefore = getChatsBefore(
                chatRoomKey,
                Long.parseLong(pageToken),
                DEFAULT_PAGE_SIZE
        );

        // Cache 에 데이터가 더 있는 경우
        if (chatBefore.size() > DEFAULT_PAGE_SIZE) {
            var lastData = chatBefore.get(chatBefore.size() - 1);
            chatBefore.remove(chatBefore.size() - 1);
            String nextPageToken = String.valueOf(lastData.id());
            return PageInfo.of(nextPageToken, chatBefore, true);
        }

        Long lastChatId = Long.parseLong(pageToken);
        int pendingCount = DEFAULT_PAGE_SIZE - chatBefore.size();
        if (chatBefore.size() != 0) {
             lastChatId = chatBefore.get(chatBefore.size() - 1).id() - 1;
        }

        var chatDb = chatRepository.findAll(
                chatRoomId,
                loginMember.getId(),
                String.valueOf(lastChatId),
                pendingCount
        );

        // TODO: Write Lua Script
        if (chatDb.hasNext()) {
            chatDb.data().forEach(chat -> zSetOps.add(
                    chatRoomKey,
                    jsonUtil.convertToJson(chat),
                    chat.id()
            ));
            chatBefore.addAll(chatDb.data());
            return PageInfo.of(chatDb.pageToken(), chatBefore, true);
        }

        chatBefore.addAll(chatDb.data());
        chatDb.data().forEach(chat -> zSetOps.add(
                chatRoomKey,
                jsonUtil.convertToJson(chat),
                chat.id()
        ));
        return PageInfo.of(chatDb.pageToken(), chatBefore, false);
    }

    private List<ChatMember> findChatMember(Long chatRoomId) {
        return chatMemberRepository.findByChatRoomId(chatRoomId);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
    }

    private boolean isMemberInChatRoom(List<ChatMember> chatMembers, Long memberId) {
        for (ChatMember chatMember : chatMembers) {
            if (chatMember.getMemberId().equals(memberId)) {
                return true;
            }
        }
        return false;
    }

    private ChatRoomResponse getChatRoomResponse(
            Long memberId,
            ChatRoom chatRoom
    ) {
        return chatRoomRepository.findChatRoomByPartnerId(
                chatRoom.getId(),
                memberId).orElseThrow(
                () -> new BadRequestException(MEMBER_NOT_IN_CHAT_ROOM)
        );
    }

    private List<ChatResponse> getChatsBefore(
            String key,
            Long lastScore,
            Integer limit
    ) {
        var chats = redisTemplate.opsForZSet()
                .reverseRangeByScore(key, -Double.MAX_VALUE, lastScore, 0, limit);

        List<ChatResponse> chatResponses = new ArrayList<>();
        if (chats != null) {
            for (var entry : chats) {
                ChatResponse chat = jsonUtil.convertToObject(
                        entry.toString(),
                        ChatResponse.class
                );
                chatResponses.add(chat);
            }
            redisTemplate.expire(key, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        return chatResponses;
    }
}
