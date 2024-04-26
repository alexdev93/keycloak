package com.example.outh2.config;

import com.example.outh2.model.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public CredentialRepresentation credentialRepresentation(){
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType("password");
        return passwordCred;
    }
    @Bean
    public UserRepresentation userRepresentation(){
        return new UserRepresentation();
    }
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

}
