package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;

public class PlayerAuthService implements AuthService {
    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;

    public PlayerAuthService(UserAccountRepository accountRepo, PlayerRepository playerRepo, PasswordEncoder encoder) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
	UserAccount account = accountRepo.findByEmail(request.email())
		.orElseThrow(() -> new RuntimeException("Invalid credentials"));

	if (!encoder.matches(request.password(), account.getPassword())) {
	    throw new RuntimeException("Invalid credentials");
	}

	Player player = playerRepo.findByAccount(account)
		.orElseThrow(() -> new RuntimeException("Account exists but not linked to a player"));

	return new LoginResponse("valid_token", account);
    }

}
