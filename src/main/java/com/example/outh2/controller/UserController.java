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

import javax.ws.rs.core.Response;
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

    @ResponseBody
    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @ResponseBody
    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDto) {
    return userService.signIn(loginDto);
    }
}
