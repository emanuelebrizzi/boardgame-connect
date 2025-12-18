package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;

@Service
public class AssociationAuthService implements AuthService<AssociationDto> {
    private final UserAccountRepository accountRepo;
    private final AssociationRepository associationRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AssociationAuthService(UserAccountRepository accountRepo, AssociationRepository associationRepo,
	    PasswordEncoder encoder, UserMapper userMapper, JwtService jwtService) {
	this.accountRepo = accountRepo;
	this.associationRepo = associationRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
	this.jwtService = jwtService;

    }

    @Override
    public LoginResponse<AssociationDto> login(LoginRequest request) {
	UserAccount account = accountRepo.findByEmail(request.email())
		.orElseThrow(() -> new RuntimeException("Invalid credentials"));

	if (!encoder.matches(request.password(), account.getPassword())) {
	    throw new RuntimeException("Invalid credentials");
	}

	Association association = associationRepo.findByAccount(account)
		.orElseThrow(() -> new RuntimeException("Account exists but not linked to a player"));

	String token = jwtService.generateToken(account);
	AssociationDto profile = userMapper.toDto(association);
	return new LoginResponse<>(token, profile);
    }

}
