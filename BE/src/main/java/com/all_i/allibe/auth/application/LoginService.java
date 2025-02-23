package com.all_i.allibe.auth.application;

import com.all_i.allibe.auth.domain.AuthTokens;
import com.all_i.allibe.auth.domain.RefreshToken;
import com.all_i.allibe.auth.domain.RefreshTokenRepository;
import com.all_i.allibe.auth.dto.request.LoginRequest;
import com.all_i.allibe.auth.infrastructure.KakaoOAuthProvider;
import com.all_i.allibe.auth.infrastructure.KakaoUserInfo;
import com.all_i.allibe.auth.util.JwtUtil;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import com.all_i.allibe.member.dto.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.all_i.allibe.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final KakaoOAuthProvider kakaoOAuthProvider;

    public AuthTokens login(LoginRequest loginRequest) {
        String kakaoAccessToken = kakaoOAuthProvider.fetchKakaoAccessToken(loginRequest.code());
        KakaoUserInfo userInfo = kakaoOAuthProvider.getUserInfo(kakaoAccessToken);

        Member member = findOrCreateMember(
               userInfo.getSocialId(),
               userInfo.getNickname(),
                userInfo.getProfileImageUrl()
        );

        AuthTokens authTokens = jwtUtil.createLoginToken(member.getId().toString());
        RefreshToken refreshToken = new RefreshToken(member.getId(), authTokens.refreshToken());
        refreshTokenRepository.save(refreshToken);
        return authTokens;
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    public String reissueAccessToken(String refreshToken, String authHeader) {
        String accessToken = authHeader.split(" ")[1];

        jwtUtil.validateRefreshToken(refreshToken);

        if (jwtUtil.isAccessTokenValid(accessToken)) {
            return accessToken;
        }

        if (jwtUtil.isAccessTokenExpired(accessToken)) {
            RefreshToken foundRefreshToken = refreshTokenRepository.findById(refreshToken)
                    .orElseThrow(() -> new BadRequestException(INVALID_REFRESH_TOKEN));
            return jwtUtil.reissueAccessToken(foundRefreshToken.getUserId().toString());
        }

        throw new BadRequestException(FAILED_TO_VALIDATE_TOKEN);
    }

    private Member findOrCreateMember(String socialId, String nickname, String profileImageUrl) {
        return memberRepository.findBySocialId(socialId)
                .orElseGet(() -> createUser(socialId, nickname, profileImageUrl));
    }

    private Member createUser(String socialId, String nickname, String profileImageUrl) {
        String nickName = generateNewUserNickname(socialId, nickname);
        log.info("nickname={}", nickName);
        return memberRepository.save(Member.of(socialId, nickName, profileImageUrl));
    }

    private String generateNewUserNickname(String socialLoginId, String nickname) {
        return nickname + "#" + socialLoginId;
    }

    @Transactional
    public void signup(SignupRequest signupRequest, Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

        member.signUp(
                signupRequest.age(),
                signupRequest.name(),
                signupRequest.email()
        );
    }
}
