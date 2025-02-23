package com.all_i.allibe.auth.presentation;

import com.all_i.allibe.auth.application.LoginService;
import com.all_i.allibe.auth.domain.AuthTokens;
import com.all_i.allibe.auth.dto.request.LoginRequest;
import com.all_i.allibe.auth.dto.response.AccessTokenResponse;
import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.dto.request.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private static final int ONE_WEEK_SECONDS = 604800;
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOKEN = "access_token";

    private final LoginService loginService;

    @PostMapping(value = "/login/kakao")
    public ResponseEntity<AccessTokenResponse> kakaoLogin(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        AuthTokens authTokens = loginService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, authTokens.refreshToken())
                .maxAge(ONE_WEEK_SECONDS)
//                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .domain(".alli.com")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new AccessTokenResponse(authTokens.accessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissueToken(
            @CookieValue(REFRESH_TOKEN) String refreshToken,
            @RequestHeader("Authorization") String authHeader
    ) {
        String reissuedToken = loginService.reissueAccessToken(refreshToken, authHeader);
        return ResponseEntity.ok(new AccessTokenResponse(reissuedToken));
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(REFRESH_TOKEN) String refreshToken
    ) {

        loginService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(
            @AuthMember Member member,
            @RequestBody SignupRequest signupRequest
    ) {
        loginService.signup(signupRequest, member);
        return ResponseEntity.noContent().build();
    }
}