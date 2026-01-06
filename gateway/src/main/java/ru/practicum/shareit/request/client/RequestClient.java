package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.client.BaseClient;
import ru.practicum.shareit.request.dto.NewRequestDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findByRequesterId(Long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> create(NewRequestDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(long itemId, NewRequestDto dto, Long userId) {
        return patch("/" + userId, userId, dto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }
}