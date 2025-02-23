package com.all_i.allibe.member.dto.request;

public record SignupRequest(
        int age,
        String name,
        String email
) {
}
