package com.example.outh2.service;

import com.example.outh2.config.KeycloakConfig;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakConfig keycloak;

    public void createUser() {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername("beza123");
        userRepresentation.setFirstName("bezawit");
        userRepresentation.setLastName("moke");
        userRepresentation.setEmail("bez@email.com");
        userRepresentation.setEnabled(true);

        Response response = keycloak.getRealm().users().create(userRepresentation);
        //If user is created successfully 200 is returned for response status.

        //Set password flow
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        String userId = CreatedResponseUtil.getCreatedId(response);
        passwordCred.setTemporary(false);
        passwordCred.setType("password");
        passwordCred.setValue("some-password");
        UserResource userResource = keycloak.getRealm().users().get(userId);
        userResource.resetPassword(passwordCred);
    }

    public List<UserRepresentation> getUsers(){
        return keycloak.getRealm().users().list();
    }
}
