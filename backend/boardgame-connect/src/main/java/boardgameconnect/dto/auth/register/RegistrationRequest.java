package boardgameconnect.dto.auth.register;

import boardgameconnect.model.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest<T>(@NotNull Email email,

	@NotEmpty String password,

	@NotEmpty String name,

	@Valid T details) {
}