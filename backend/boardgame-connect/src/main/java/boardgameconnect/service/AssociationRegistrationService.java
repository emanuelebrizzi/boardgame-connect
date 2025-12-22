package boardgameconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.authorization.AssociationDetails;
import boardgameconnect.dto.authorization.RegistrationRequest;
import boardgameconnect.model.Association;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@Service
public class AssociationRegistrationService implements RegistrationService<AssociationDetails> {

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
    public void register(RegistrationRequest<AssociationDetails> request) {
	if (accountRepo.findByEmail(request.email()).isPresent()) {
	    throw new RuntimeException("Email already registered");
	}

	UserAccount account = new UserAccount(request.email(), encoder.encode(request.password()), request.name(),
		UserRole.ASSOCIATION);

	Association association = new Association(account, request.details().taxCode(), request.details().address());
	associationRepo.save(association);
    }
}
