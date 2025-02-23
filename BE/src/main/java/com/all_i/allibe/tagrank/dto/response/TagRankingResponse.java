package com.all_i.allibe.tagrank.dto.response;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagRankingResponse(
        String tag,
        int count
) {
}
