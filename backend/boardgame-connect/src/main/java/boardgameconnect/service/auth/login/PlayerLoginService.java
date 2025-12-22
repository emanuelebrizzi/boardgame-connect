package boardgameconnect.service.auth.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.PlayerProfile;
import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;
import boardgameconnect.exception.InvalidCredentialsException;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.service.JwtService;

@Service
public class PlayerLoginService implements LoginService<PlayerProfile> {
    private final UserAccountRepository accountRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public PlayerLoginService(UserAccountRepository accountRepo, PlayerRepository playerRepo, PasswordEncoder encoder,
	    UserMapper userMapper, JwtService jwtService) {
	this.accountRepo = accountRepo;
	this.playerRepo = playerRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
	this.jwtService = jwtService;
    }

    @Override
    public LoginResponse<PlayerProfile> login(LoginRequest request) {
	UserAccount account = accountRepo.findByEmail(request.email())
		.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

	if (!encoder.matches(request.password(), account.getPassword())) {
	    throw new InvalidCredentialsException("Invalid credentials");
	}

	Player player = playerRepo.findByAccount(account)
		.orElseThrow(() -> new RuntimeException("Account exists but not linked to a player"));

	String token = jwtService.generateToken(account);
	PlayerProfile profile = userMapper.toDto(player);
	return new LoginResponse<>(token, profile);
    }

}
