package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.dto.PlayerRegistrationDetails;
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class PlayerRegistrationService implements RegistrationService<PlayerDto, PlayerRegistrationDetails> {

    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public PlayerRegistrationService(UserAccountRepository accountRepo, PlayerRepository playerRepo,
	    PasswordEncoder encoder, UserMapper userMapper) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public PlayerDto register(RegistrationRequest<PlayerRegistrationDetails> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already registered");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()), request.username(),
		UserRole.PLAYER);
	UserAccount savedAccount = accountRepo.save(account);
	Player player = new Player(savedAccount);
	Player savedPlayer = playerRepo.save(player);

	return userMapper.toDto(savedPlayer);
    }
}