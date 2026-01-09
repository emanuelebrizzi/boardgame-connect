package boardgameconnect.service.association;

import java.util.List;

import boardgameconnect.dto.AssociationSummary;

public interface AssociationService {

	List<AssociationSummary> getAssociations();

	List<AssociationSummary> getAssociations(String boardgameId);

}
