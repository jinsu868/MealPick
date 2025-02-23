package com.all_i.allibe.chat.application;

import com.all_i.allibe.chat.domain.Chat;
import com.all_i.allibe.chat.domain.ChatMember;
import com.all_i.allibe.chat.domain.ChatRoom;
import com.all_i.allibe.chat.domain.repository.ChatMemberRepository;
import com.all_i.allibe.chat.domain.repository.ChatRepository;
import com.all_i.allibe.chat.domain.repository.ChatRoomRepository;
import com.all_i.allibe.chat.dto.ChatRoomCreateEvent;
import com.all_i.allibe.chat.dto.ChatSummary;
import com.all_i.allibe.chat.dto.request.ChatRoomCreateRequest;
import com.all_i.allibe.chat.dto.response.ChatMemberQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomCreateResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.chat.dto.response.ChatQueryResponse;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.util.JsonUtil;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import com.all_i.allibe.member.domain.vo.MemberSummary;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.all_i.allibe.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private static final Long NOT_EXIST_CHAT_ROOM_BETWEEN_MEMBER = -1L;
    private static final int KEY_EXPIRE_TIME = 604800;
    private static final String MEMBERS_CHAT_ROOMS_KEY = "members:%s:rooms";

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonUtil jsonUtil;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 채팅방 생성후 입장 메시지를 생성한다.
     * 해당 유저의 채팅방 캐시가 존재할 경우 캐시에 생성한 채팅방을 추가한다.
     */
    @Transactional
    public ChatRoomCreateResponse createChatRoom(
            ChatRoomCreateRequest chatRoomCreateRequest,
            Member loginMember
    ) {
        if (!memberRepository.existsById(chatRoomCreateRequest.partnerId())) {
            throw new BadRequestException(MEMBER_NOT_FOUND);
        }

        Long existChatRoomId = getExistChatRoomIdBetweenMember(
                loginMember.getId(),
                chatRoomCreateRequest.partnerId()
        );

        if (existChatRoomId != NOT_EXIST_CHAT_ROOM_BETWEEN_MEMBER) {
            return ChatRoomCreateResponse.of(
                    existChatRoomId,
                    true
            );
        }

        Member partner = memberRepository.findById(chatRoomCreateRequest.partnerId())
                .orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.from(chatRoomCreateRequest.name());
        chatRoomRepository.save(chatRoom);

        Chat myEnterChat = Chat.createChat(loginMember, chatRoom.getId());
        Chat partnerEnterChat = Chat.createChat(partner, chatRoom.getId());

        chatRepository.save(myEnterChat);
        chatRepository.save(partnerEnterChat);

        ChatMember chatSelf = ChatMember.createChatMember(
                loginMember.getId(),
                chatRoom.getId(),
                myEnterChat.getId()
        );
        ChatMember chatPartner = ChatMember.createChatMember(
                chatRoomCreateRequest.partnerId(),
                chatRoom.getId(),
                partnerEnterChat.getId()
        );

        chatMemberRepository.save(chatSelf);
        chatMemberRepository.save(chatPartner);

        var myChatRoomResponse = new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                partnerEnterChat.getId(),
                partner.getName(),
                partnerEnterChat.getContent(),
                partnerEnterChat.getCreatedAt(),
                partner.getProfileImage()
        );
        var partnerChatRoomResponse = new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                myEnterChat.getId(),
                loginMember.getName(),
                myEnterChat.getContent(),
                myEnterChat.getCreatedAt(),
                loginMember.getProfileImage()
        );

        eventPublisher.publishEvent(
                new ChatRoomCreateEvent(
                        loginMember.getId(),
                        chatRoomCreateRequest.partnerId(),
                        myChatRoomResponse,
                        partnerChatRoomResponse
                )
        );

        return ChatRoomCreateResponse.of(
                chatRoom.getId(),
                false
        );
    }

    /**
     * 캐시가 있으면 캐시에서 읽고 없으면 DB 에서 채팅방을 읽은 후 캐시에 적재한다.
     */
    // Cache Hit
    public List<ChatRoomResponse> findAll(Long loginMemberId) {
        String key = String.format(MEMBERS_CHAT_ROOMS_KEY, loginMemberId);
        var zSetOps = redisTemplate.opsForZSet();
        if (redisTemplate.hasKey(key)) {
            var chatRoomCache = zSetOps.reverseRangeWithScores(key, 0, -1);
            redisTemplate.expire(key, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
            return chatRoomCache.stream()
                    .map(entry -> jsonUtil.convertToObject(
                            entry.getValue().toString(),
                            ChatRoomResponse.class))
                    .toList();
        }

        // DB Read
        var chatRooms = findChatRooms(loginMemberId);
        var chatRoomIdToLatestChatId = mapChatRoomIdToLatestChatId(chatRooms);
        var chatIds = extractChatIds(chatRooms);
        var chatRoomIds = extractChatRoomIds(chatRooms);
        var chatRoomIdToYourInfo = mapChatRoomIdToYourInfo(loginMemberId, chatRoomIds);
        var chatIdToContent = mapChatIdToChatContent(chatIds);
        var chatRoomResponses = chatRooms
                .stream()
                .map(queryResponse -> {
                    Long latestChatId = chatRoomIdToLatestChatId.get(queryResponse.id());
                    ChatSummary chatSummary = Optional.ofNullable(latestChatId)
                            .map(chatIdToContent::get)
                            .orElse(null);

                    return new ChatRoomResponse(
                            queryResponse.id(),
                            queryResponse.name(),
                            queryResponse.displayIdx(),
                            chatRoomIdToYourInfo.get(queryResponse.id()).name(),
                            chatSummary != null ? chatSummary.content() : "",
                            chatSummary != null ? chatSummary.createdAt() : null,
                            chatRoomIdToYourInfo.get(queryResponse.id()).profileImage()
                    );
                })
                .toList();

        // Cache Write
        chatRoomResponses.forEach(chatRoomResponse ->
                zSetOps.add(
                        key,
                        jsonUtil.convertToJson(chatRoomResponse),
                        chatRoomResponse.displayIdx()
                )
        );
        redisTemplate.expire(key, KEY_EXPIRE_TIME, TimeUnit.SECONDS);

        return chatRoomResponses;
    }

    private Map<Long, MemberSummary> mapChatRoomIdToYourInfo(
            Long memberId,
            List<Long> chatRoomIds
    ) {
        return chatMemberRepository.findAllByChatRoomIdIn(chatRoomIds).stream()
                .filter(chatMember ->
                        !chatMember.memberId().equals(memberId))
                .collect(Collectors.toMap(
                        ChatMemberQueryResponse::chatRoomId,
                        chatMember -> MemberSummary.of(
                                chatMember.name(),
                                chatMember.profileImageUrl()
                        ))
                );
    }

    private List<Long> extractChatRoomIds(List<ChatRoomQueryResponse> chatRooms) {
        return chatRooms.stream()
                .map(ChatRoomQueryResponse::id)
                .toList();
    }

    private Map<Long, ChatSummary> mapChatIdToChatContent(List<Long> chatIds) {
        return chatRepository.findByChatIdIn(chatIds).stream()
                .collect(Collectors.toMap(
                        ChatQueryResponse::id,
                        chat -> new ChatSummary(chat.content(), chat.createdAt())
        ));
    }

    private Map<Long, Long> mapChatRoomIdToLatestChatId(List<ChatRoomQueryResponse> chatRooms) {
        return chatRooms.stream()
                .collect(Collectors.toMap(
                        ChatRoomQueryResponse::id,
                        ChatRoomQueryResponse::displayIdx
                ));
    }

    private List<Long> extractChatIds(List<ChatRoomQueryResponse> chatRooms) {
        return chatRooms
                .stream()
                .map(ChatRoomQueryResponse::displayIdx)
                .toList();
    }

    private Long getExistChatRoomIdBetweenMember(
            Long ownerId,
            Long visitorId
    ) {
        var ownerChatMembers = chatMemberRepository.findByMemberId(ownerId);
        var visitorChatMembers = chatMemberRepository.findByMemberId(visitorId);

        for (ChatMember ownerChatMember : ownerChatMembers) {
            for (ChatMember visitorChatMember : visitorChatMembers) {
                if (ownerChatMember.getChatRoomId().equals(visitorChatMember.getChatRoomId())) {
                    return ownerChatMember.getChatRoomId();
                }
            }
        }

        return NOT_EXIST_CHAT_ROOM_BETWEEN_MEMBER;
    }

    private List<ChatRoomQueryResponse> findChatRooms(Long memberId) {
        return chatRoomRepository.findAll(memberId);
    }
}
