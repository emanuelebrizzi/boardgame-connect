package boardgameconnect.service.auth.register;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.auth.register.RegisterRequest;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class PlayerRegisterService implements RegisterService<Void> {

    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;

    public PlayerRegisterService(UserAccountRepository accountRepo, PlayerRepository playerRepo,
	    PasswordEncoder encoder) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
    }

    @Override
    public void register(RegisterRequest<Void> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already registered");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()), request.name(),
		UserRole.PLAYER);

	Player player = new Player(account);
	playerRepo.save(player);

    }
}