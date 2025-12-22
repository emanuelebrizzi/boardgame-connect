package boardgameconnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import boardgameconnect.dto.AssociationProfile;
import boardgameconnect.dto.PlayerProfile;
import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;
import boardgameconnect.service.auth.login.AssociationLoginService;
import boardgameconnect.service.auth.login.PlayerLoginService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final PlayerLoginService playerService;
    private final AssociationLoginService associationService;

    public AuthenticationController(PlayerLoginService playerService, AssociationLoginService associationService) {
	this.playerService = playerService;
	this.associationService = associationService;
    }

    @PostMapping("/login/player")
    public ResponseEntity<LoginResponse<PlayerProfile>> loginPlayer(@RequestBody LoginRequest request) {
	return ResponseEntity.ok(playerService.login(request));
    }

    @PostMapping("/login/association")
    public ResponseEntity<LoginResponse<AssociationProfile>> loginAssociation(@RequestBody LoginRequest request) {
	return ResponseEntity.ok(associationService.login(request));
    }

}
