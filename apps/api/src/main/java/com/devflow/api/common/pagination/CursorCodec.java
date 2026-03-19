package com.devflow.api.common.pagination;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public CursorCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String encode(T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ResponseCode.INTERNAL_ERROR);
        }
    }

    public <T> T decode(String cursor, Class<T> type) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            return objectMapper.readValue(decoded, type);
        } catch (RuntimeException | JsonProcessingException exception) {
            throw new BusinessException(ResponseCode.INVALID_CURSOR);
        }
    }
}
