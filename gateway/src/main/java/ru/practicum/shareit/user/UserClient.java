package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> createUser(UserCreateRequestDto userCreateRequestDto) {
        return post("", userCreateRequestDto);
    }

    public ResponseEntity<Object> updateUser(Long userId,
                                             UserUpdateRequestDto userUpdateRequestDto) {
        return patch("/" + userId, userUpdateRequestDto);
    }

    public ResponseEntity<Object> deleteUserById(Long userId) {
        return delete("/" + userId);
    }
}