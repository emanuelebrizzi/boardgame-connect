package boardgameconnect.mapper;

import org.springframework.stereotype.Component;

import boardgameconnect.dto.PlayerProfile;
import boardgameconnect.dto.association.AssociationProfile;
import boardgameconnect.model.Association;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserRole;

@Component
public class UserMapper {

	public PlayerProfile toDto(Player player) {
		return new PlayerProfile(player.getId(), player.getAccount().getEmail().getEmail(),
				player.getAccount().getName(), UserRole.PLAYER);
	}

	public AssociationProfile toDto(Association association) {
		return new AssociationProfile(association.getId(), association.getAccount().getEmail().getEmail(),
				association.getAccount().getName(), association.getTaxCode(), association.getAddress(),
				UserRole.ASSOCIATION);
	}
}
