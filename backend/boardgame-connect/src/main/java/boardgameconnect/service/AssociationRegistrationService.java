package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.AssociationRegistrationDetails;
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class AssociationRegistrationService
	implements RegistrationService<AssociationDto, AssociationRegistrationDetails> {

    private final UserAccountRepository accountRepo;
    private final AssociationRepository associationRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public AssociationRegistrationService(UserAccountRepository accountRepo, AssociationRepository associationRepo,
	    PasswordEncoder encoder, UserMapper userMapper) {
	this.accountRepo = accountRepo;
	this.associationRepo = associationRepo;
	this.encoder = encoder;
	this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public AssociationDto register(RegistrationRequest<AssociationRegistrationDetails> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already in use");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()), request.username(),
		UserRole.ASSOCIATION);

	AssociationRegistrationDetails details = request.details();
	Association association = new Association(account, details.taxCode(), details.address());
	Association savedAssociation = associationRepo.save(association);

	return userMapper.toDto(savedAssociation);
    }
}
