package boardgameconnect.service.auth.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.association.AssociationProfile;
import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.InvalidCredentialsException;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;
import boardgameconnect.service.JwtService;

@Service
public class AssociationLoginService implements LoginService<AssociationProfile> {
	private final UserAccountRepository accountRepo;
	private final AssociationRepository associationRepo;
	private final PasswordEncoder encoder;
	private final UserMapper userMapper;
	private final JwtService jwtService;

	public AssociationLoginService(UserAccountRepository accountRepo, AssociationRepository associationRepo,
			PasswordEncoder encoder, UserMapper userMapper, JwtService jwtService) {
		this.accountRepo = accountRepo;
		this.associationRepo = associationRepo;
		this.encoder = encoder;
		this.userMapper = userMapper;
		this.jwtService = jwtService;

	}

	@Override
	public LoginResponse<AssociationProfile> login(LoginRequest request) {
		UserAccount account = accountRepo.findByEmail(request.email())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

		if (!encoder.matches(request.password(), account.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		Association association = associationRepo.findByAccount(account)
				.orElseThrow(() -> new AssociationNotFoundException("Account exists but not linked to a player"));

		String token = jwtService.generateToken(account);
		AssociationProfile profile = userMapper.toDto(association);
		return new LoginResponse<>(token, profile);
	}

}
