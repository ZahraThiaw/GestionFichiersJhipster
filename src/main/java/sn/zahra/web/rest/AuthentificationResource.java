package sn.zahra.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.zahra.service.dto.LoginkeycloakDTORequest;
import sn.zahra.service.dto.LoginkeycloakDTOResponse;
import sn.zahra.service.impl.KeycloakAuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationResource {

    private final KeycloakAuthService keycloakAuthService;

    public AuthentificationResource(KeycloakAuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginkeycloakDTOResponse> login(@RequestBody LoginkeycloakDTORequest loginRequest) {
        LoginkeycloakDTOResponse response = keycloakAuthService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }
}
