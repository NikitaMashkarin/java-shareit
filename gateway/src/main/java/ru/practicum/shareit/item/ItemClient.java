package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.comment.CommentItemRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemCreateRequestDto itemCreateRequestDto) {
        return post("", userId, itemCreateRequestDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateRequestDto itemUpdateRequestDto) {
        return patch("/" + itemId, userId, itemUpdateRequestDto);
    }

    public ResponseEntity<Object> getAllUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemsByText(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentItemRequestDto commentItemRequestDto) {
        return post("/" + itemId + "/comment", userId, commentItemRequestDto);
    }
}