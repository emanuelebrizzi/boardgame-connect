package boardgameconnect.mapper;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.model.Association;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserRole;

@Component
public class UserMapper {

    public PlayerDto toDto(Player player) {
	return new PlayerDto(player.getId(), player.getAccount().getEmail().getEmail(), player.getAccount().getName(),
		UserRole.PLAYER);
    }

    public AssociationDto toDto(Association association) {
	return new AssociationDto(association.getId(), association.getAccount().getEmail().getEmail(),
		association.getAccount().getName(), association.getTaxCode(), association.getAddress(),
		UserRole.ASSOCIATION);
    }
}
