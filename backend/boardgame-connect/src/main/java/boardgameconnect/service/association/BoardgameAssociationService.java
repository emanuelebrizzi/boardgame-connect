package boardgameconnect.service.association;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.mapper.AssociationMapper;

@Service
public class BoardgameAssociationService implements AssociationService {

	private final AssociationRepository associationRepository;
	private final AssociationMapper associationMapper;

	public BoardgameAssociationService(AssociationRepository associationRepository,
			AssociationMapper associationMapper) {
		this.associationRepository = associationRepository;
		this.associationMapper = associationMapper;
	}

	@Override
	public List<AssociationSummary> getAllAssociations() {
		return associationRepository.findAll().stream().map(associationMapper::toDto).collect(Collectors.toList());
	}

}
