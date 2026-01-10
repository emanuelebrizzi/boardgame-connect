package boardgameconnect.mapper;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.model.Boardgame;

@Component
public class BoardgameMapper {

	public BoardgameDto toDto(Boardgame bg) {
		if (bg == null)
			return null;

		return new BoardgameDto(bg.getId(), bg.getName(), bg.getMinPlayer(), bg.getMaxPlayer(), bg.getMinTimeInMin(),
				bg.getTimeInMinPerPlayer(), bg.getImagePath());
	}
}