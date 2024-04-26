package com.example.outh2.service;

import com.example.outh2.config.Keycloak;
import com.example.outh2.exception.CustomException;
import com.example.outh2.model.LoginDTO;
import com.example.outh2.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;
    private final UserRepresentation userRepresentation;
    private final CredentialRepresentation passwordMapper;
    private final WebClient webClient;


    public ResponseEntity<Void> createUser(User user) {

        passwordMapper.setValue(user.getPassword());
        userRepresentation.setUsername(user.getUserName());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setCredentials(List.of(passwordMapper));
        userRepresentation.setEnabled(true);
        List<String> roles = new ArrayList<>();
        roles.add("user");
        userRepresentation.setRealmRoles(roles);

        try{
            Response response = keycloak.getRealm().users().create(userRepresentation);

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("User created successfully");
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        }catch (Exception ex) {
            throw new CustomException(ex.getMessage(), ex.getCause());
        };
        throw new CustomException("user representation is not correct or duplicated");
    }

    public ResponseEntity<?> signIn(LoginDTO loginDTO){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloak.getClientId());
        body.add("client_secret", keycloak.getClientSecret());
        body.add("username", loginDTO.getUsername());
        body.add("password", loginDTO.getPassword());
        body.add("grant_type", "password");

        return webClient
                .post()
                .uri("http://localhost:8080/realms/safari/protocol/openid-connect/token")
                .headers(h -> h.addAll(headers))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public List<UserRepresentation> getUsers() {
        return keycloak.getRealm().users().list();
    }
}
