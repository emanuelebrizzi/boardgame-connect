package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.dto.authorization.RegistrationRequest;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class PlayerRegistrationService implements RegistrationService<PlayerDto> {

    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;

    public PlayerRegistrationService(UserAccountRepository accountRepo, PlayerRepository playerRepo,
	    PasswordEncoder encoder) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
    }

    @Override
    @Transactional
    public void register(RegistrationRequest<PlayerDto> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already registered");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()),
		request.details().name(), UserRole.PLAYER);
	accountRepo.save(account);

	Player player = new Player(account);
	playerRepo.save(player);

    }
}