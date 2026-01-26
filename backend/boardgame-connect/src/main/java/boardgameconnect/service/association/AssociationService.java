package boardgameconnect.service.association;

import java.util.List;

import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.dto.GameTableRequest;
import boardgameconnect.dto.GameTableResponse;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.model.Email;

public interface AssociationService {

	List<AssociationSummary> getAssociations();

	List<AssociationSummary> getAssociations(String boardgameId);

	void addBoardgamesToAssociation(List<String> boardgamesIds, Email associationEmail);

	void removeBoardgamesFromAssociation(List<String> boardgameIds, Email association1Email);

	List<BoardgameDto> getBoardgamesFrom(Email associationEmail);

	void addTableToAssociation(GameTableRequest request, Email associationEmail);

	void removeTableFromAssociation(String tableId, Email associationEmail);

	List<GameTableResponse> getAssociationTablesById(String id);

	List<GameTableResponse> getAssociationTablesByEmail(Email associationEmail);

}
