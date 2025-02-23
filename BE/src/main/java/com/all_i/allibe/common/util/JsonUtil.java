package com.all_i.allibe.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtil {

    private  final ObjectMapper objectMapper;

    public String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화에 실패했습니다.");
            throw new RuntimeException(e);
        }
    }

    public <T> T convertToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonMappingException e) {
            log.error("JSON 매핑 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> convertToObjects(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            log.error("JSON 리스트 매핑 실패: {}", e.getMessage());
            return null;
        }
    }
}
