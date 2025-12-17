package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;

@Service
public class PlayerAuthService implements AuthService<PlayerDto> {
    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public PlayerAuthService(UserAccountRepository accountRepo, PlayerRepository playerRepo, PasswordEncoder encoder,
	    UserMapper userMapper) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
    }

    @Override
    public LoginResponse<PlayerDto> login(LoginRequest request) {
	UserAccount account = accountRepo.findByEmail(request.email())
		.orElseThrow(() -> new RuntimeException("Invalid credentials"));

	if (!encoder.matches(request.password(), account.getPassword())) {
	    throw new RuntimeException("Invalid credentials");
	}

	Player player = playerRepo.findByAccount(account)
		.orElseThrow(() -> new RuntimeException("Account exists but not linked to a player"));

	PlayerDto profile = userMapper.toDto(player);
	return new LoginResponse<>("valid_token", profile);
    }

}
