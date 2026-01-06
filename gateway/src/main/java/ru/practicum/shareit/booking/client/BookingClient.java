package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.common.client.BaseClient;

import java.util.Map;
import java.util.Optional;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findAll(Long ownerId, Optional<BookingStatus> status) {
        return get("", ownerId, Map.of("status", status));
    }

    public ResponseEntity<Object> findByOwnerId(Long ownerId, Optional<BookingStatus> status) {
        return get("/owner", ownerId, Map.of("status", status));
    }

    public ResponseEntity<Object> create(NewBookingDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approve(long itemId, boolean approved, long userId) {
        return patch("/" + itemId + "?approved=" + approved, userId);
    }
}
