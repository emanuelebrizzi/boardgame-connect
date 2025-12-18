package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.exception.InvalidCredentialsException;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;

@Service
public class PlayerAuthService implements AuthService<PlayerDto> {
    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public PlayerAuthService(UserAccountRepository accountRepo, PlayerRepository playerRepo, PasswordEncoder encoder,
	    UserMapper userMapper, JwtService jwtService) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
	this.jwtService = jwtService;
    }

    @Override
    public LoginResponse<PlayerDto> login(LoginRequest request) {
	UserAccount account = accountRepo.findByEmail(request.email())
		.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

	if (!encoder.matches(request.password(), account.getPassword())) {
	    throw new InvalidCredentialsException("Invalid credentials");
	}

	Player player = playerRepo.findByAccount(account)
		.orElseThrow(() -> new RuntimeException("Account exists but not linked to a player"));

	String token = jwtService.generateToken(account);
	PlayerDto profile = userMapper.toDto(player);
	return new LoginResponse<>(token, profile);
    }

}
