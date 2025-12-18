package boardgameconnect.dto;

import boardgameconnect.model.UserRole;

public record AssociationDto(String id, String email, String name, String taxCode, String address, UserRole role) {
}
