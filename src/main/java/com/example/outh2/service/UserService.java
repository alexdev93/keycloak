package com.example.outh2.service;

import com.example.outh2.exception.CustomException;
import com.example.outh2.model.LoginDTO;
import com.example.outh2.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepresentation userRepresentation;
    private final CredentialRepresentation passwordMapper;
    private final WebClient webClient;
    private final ModelMapper modelMapper;
    private final UsersResource usersResource;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    public List<User> getUsers() {
        List<UserRepresentation> userRepresentations = usersResource.list();
        List<User> userList = new ArrayList<>();
        for (UserRepresentation userRep : userRepresentations) {
            User user = new User();
            user.setUserName(userRep.getUsername());
            user.setEmail(userRep.getEmail());
            user.setFirstName(userRep.getFirstName());
            user.setLastName(userRep.getLastName());
            user.setId(userRep.getId());
            // You should add the 'user' object to the list
            userList.add(user);
        }
        return userList;
    }


    public ResponseEntity<String> createUser(User user) {

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

        try {
            Response response = usersResource.create(userRepresentation);
            if (response.getStatus() != 201) {
                System.err.println("Failed to create user: " + response.getStatusInfo());
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            log.info("user created");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), ex.getCause());
        }
    }

    public List<UserRepresentation> getUser(String userName){
        return usersResource.search(userName, true);
    }

    public void updateUser(String userId, User userDTO) {
        try {
            UserRepresentation user = modelMapper.map(userDTO, UserRepresentation.class);
            usersResource.get(userId).update(user);
        }catch (Exception ex) {
            throw new CustomException(ex.getMessage(), ex.getCause());
        }
    }

    public void deleteUser(String userId){
        try {
            usersResource.get(userId)
                    .remove();
        }catch (Exception ex) {
            throw new CustomException(ex.getMessage(), ex.getCause());
        }
    }

    public void sendVerificationLink(String userId){
        try {
            usersResource.get(userId)
                    .sendVerifyEmail();
        }catch (Exception ex){
            throw new CustomException(ex.getMessage(), ex.getCause());
        }
    }

    public void sendResetPassword(String userId){
        try {
            usersResource.get(userId)
                    .executeActionsEmail(List.of("UPDATE_PASSWORD"));
        }catch (Exception ex){
            throw new CustomException(ex.getMessage(), ex.getCause());
        }
    }

    public ResponseEntity<?> signIn(LoginDTO loginDTO) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
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

}
