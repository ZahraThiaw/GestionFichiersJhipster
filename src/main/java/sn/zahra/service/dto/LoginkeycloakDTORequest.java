package sn.zahra.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginkeycloakDTORequest {

    @Schema(description = "Le username de l'utilisateur", example = "user1")
    private String username;

    @Schema(description = "Le password de l'utilisateur", example = "password123")
    private String password;
}
