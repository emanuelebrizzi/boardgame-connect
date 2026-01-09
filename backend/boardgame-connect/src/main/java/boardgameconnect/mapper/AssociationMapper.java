package boardgameconnect.mapper;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.model.Association;

@Component
public class AssociationMapper {

	public AssociationSummary toDto(Association association) {
		return new AssociationSummary(association.getId(), association.getAccount().getName(),
				association.getAddress());
	}
}
