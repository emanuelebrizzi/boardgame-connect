package boardgameconnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.dto.authorization.LoginRequest;
import boardgameconnect.dto.authorization.LoginResponse;
import boardgameconnect.service.AssociationAuthService;
import boardgameconnect.service.PlayerAuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final PlayerAuthService playerService;
    private final AssociationAuthService associationService;

    public AuthenticationController(PlayerAuthService playerService, AssociationAuthService associationService) {
	this.playerService = playerService;
	this.associationService = associationService;
    }

    @PostMapping("/login/player")
    public ResponseEntity<LoginResponse<PlayerDto>> loginPlayer(@RequestBody LoginRequest request) {
	return ResponseEntity.ok(playerService.login(request));
    }

    @PostMapping("/login/association")
    public ResponseEntity<LoginResponse<AssociationDto>> loginAssociation(@RequestBody LoginRequest request) {
	return ResponseEntity.ok(associationService.login(request));
    }

}
