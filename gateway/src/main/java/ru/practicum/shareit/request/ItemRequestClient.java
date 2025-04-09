package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        return post("", userId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getUserItemRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequests() {
        return get("/all");
    }

    public ResponseEntity<Object> getItemRequestById(Long requestId) {
        return get("/" + requestId);
    }
}