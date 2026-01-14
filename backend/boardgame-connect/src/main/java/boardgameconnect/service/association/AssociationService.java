package boardgameconnect.service.association;

import java.util.List;

import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.model.Email;

public interface AssociationService {

	List<AssociationSummary> getAssociations();

	List<AssociationSummary> getAssociations(String boardgameId);

	void addBoardgamesToAssociation(List<String> boardgamesIds, Email associationEmail);

}
