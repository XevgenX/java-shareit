package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findByUserId(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> findCommentById(Long id) {
        return get("/" + id + "/comment");
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> create(ItemDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(long itemId, ItemDto dto, Long userId) {
        return patch("/" + userId, userId, dto);
    }

    public ResponseEntity<Object> addComment(long itemId, NewCommentDto dto, Long userId) {
        return post("/" + itemId + "/comment", userId, dto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }
}
