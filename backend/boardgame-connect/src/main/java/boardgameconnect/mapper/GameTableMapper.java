package boardgameconnect.mapper;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.GameTableResponse;
import boardgameconnect.model.GameTable;

@Component
public class GameTableMapper {

	public GameTableResponse toResponse(GameTable gameTable) {
		if (gameTable == null)
			return null;

		return new GameTableResponse(gameTable.getId(), gameTable.getSize(), gameTable.getCapacity());
	}
}