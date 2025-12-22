package boardgameconnect.dto.auth.register;

import jakarta.validation.constraints.NotBlank;

public record AssociationDetails(@NotBlank String taxCode, @NotBlank String address) {

}
