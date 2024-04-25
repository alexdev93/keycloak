package com.example.outh2.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {

    private final String clientId = "fairflow";
    private final String clientSecret = "SqqoTyrdOH1tKD8EsZ2mSjwV9DxDhLcX";
    private final String username = "alex123";
    private final String password = "pass123";
    private final String realm = "safari";
    public Keycloak getAdminKeycloakUser() {
        return KeycloakBuilder.builder().serverUrl("http://localhost:8080")
                .grantType("password").realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
    }

    public RealmResource getRealm() {
        return getAdminKeycloakUser().realm(realm);
    }
}
