package boardgameconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.AssociationProfile;
import boardgameconnect.dto.PlayerProfile;
import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;
import boardgameconnect.dto.auth.register.AssociationDetails;
import boardgameconnect.dto.auth.register.RegistrationRequest;
import boardgameconnect.service.auth.login.AssociationLoginService;
import boardgameconnect.service.auth.login.PlayerLoginService;
import boardgameconnect.service.auth.register.AssociationRegistrationService;
import boardgameconnect.service.auth.register.PlayerRegistrationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

	private final PlayerLoginService playerService;
	private final AssociationLoginService associationService;
	private final PlayerRegistrationService playerRegistrationService;
	private final AssociationRegistrationService associationRegistrationService;

	public AuthenticationController(PlayerLoginService playerService, AssociationLoginService associationService,
			PlayerRegistrationService playerRegistrationService,
			AssociationRegistrationService associationRegistrationService) {
		this.playerService = playerService;
		this.associationService = associationService;
		this.playerRegistrationService = playerRegistrationService;
		this.associationRegistrationService = associationRegistrationService;
	}

	@PostMapping("/login/player")
	public ResponseEntity<LoginResponse<PlayerProfile>> loginPlayer(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(playerService.login(request));
	}

	@PostMapping("/login/association")
	public ResponseEntity<LoginResponse<AssociationProfile>> loginAssociation(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(associationService.login(request));
	}

	@PostMapping("/register/player")
	@ResponseStatus(HttpStatus.CREATED)
	public void registerPlayer(@RequestBody @Valid RegistrationRequest<Void> request) {
		playerRegistrationService.register(request);
	}

	@PostMapping("/register/association")
	@ResponseStatus(HttpStatus.CREATED)
	public void registerAssociation(@RequestBody @Valid RegistrationRequest<AssociationDetails> request) {
		associationRegistrationService.register(request);
	}
}
