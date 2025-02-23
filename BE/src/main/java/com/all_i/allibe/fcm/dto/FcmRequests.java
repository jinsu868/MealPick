package com.all_i.allibe.fcm.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FcmRequests {
    private List<Long> memberIds;
    private String title;
    private String body;
}
