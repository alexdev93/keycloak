package com.example.outh2.controller;

import com.example.outh2.model.LoginDTO;
import com.example.outh2.model.User;
import com.example.outh2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class UserController {

    private final WebClient webClient;
    private final UserService userService;

    @GetMapping(path = "/get")
    public List<UserRepresentation> getUsers(){
        return userService.getUsers();
    }

    @PostMapping(path = "/create")
    public void createUser() {

        userService.createUser();
//         String res = webClient.post()
//                .uri("http://localhost:8080/admin/realms/safari/users")
//                 .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .body(BodyInserters.fromValue(userDTO))
//                 .retrieve()
//                 .bodyToMono(String.class)
//                 .block();

//        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/login")
    @ResponseBody
    public Mono<ResponseEntity<String>> login(@RequestBody LoginDTO loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "fairflow");
        body.add("client_secret", "SqqoTyrdOH1tKD8EsZ2mSjwV9DxDhLcX");
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");

        return webClient
                .post()
                .uri("http://localhost:8080/realms/safari/protocol/openid-connect/token")
                .headers(h -> h.addAll(headers))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .toEntity(String.class);
//                .block();

    }
}
