package com.all_i.allibe.auth.presentation;

import com.all_i.allibe.auth.application.LoginService;
import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.member.dto.request.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(LoginController.class)
class LoginControllerTest extends ControllerTest {

    @MockitoBean
    LoginService loginService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    void can_signUp() throws Exception {
        var signupRequest = new SignupRequest(12, "test_name", "test_email");

        doNothing().when(loginService).signup(any(), any());

        mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(loginService).signup(any(), any());
    }
}