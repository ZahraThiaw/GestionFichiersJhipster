package sn.zahra.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sn.zahra.service.dto.LoginkeycloakDTORequest;
import sn.zahra.service.dto.LoginkeycloakDTOResponse;

@Service
public class KeycloakAuthService {

    @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    private String keycloakAuthUrl;

    @Value("${spring.security.oauth2.client.registration.oidc.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.oidc.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KeycloakAuthService() {
        this.restTemplate = new RestTemplate();
    }

    public LoginkeycloakDTOResponse authenticate(LoginkeycloakDTORequest loginRequest) {
        String tokenUrl = keycloakAuthUrl + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", loginRequest.getUsername());
        map.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<LoginkeycloakDTOResponse> response = restTemplate.postForEntity(
            tokenUrl,
            request,
            LoginkeycloakDTOResponse.class
        );

        return response.getBody();
    }
}
