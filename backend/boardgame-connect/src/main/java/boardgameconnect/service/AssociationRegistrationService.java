package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class AssociationRegistrationService implements RegistrationService<AssociationDto> {

    private final UserAccountRepository accountRepo;
    private final AssociationRepository associationRepo;
    private final PasswordEncoder encoder;

    public AssociationRegistrationService(UserAccountRepository accountRepo, AssociationRepository associationRepo,
	    PasswordEncoder encoder) {
	this.accountRepo = accountRepo;
	this.associationRepo = associationRepo;
	this.encoder = encoder;
    }

    @Override
    @Transactional
    public void register(RegistrationRequest<AssociationDto> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already registered");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()),
		request.details().name(), UserRole.ASSOCIATION);
	accountRepo.save(account);

	Association association = new Association(account, request.details().taxCode(), request.details().address());
	associationRepo.save(association);

    }
}
