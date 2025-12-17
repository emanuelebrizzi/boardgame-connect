package boardgameconnect.dto;

import boardgameconnect.model.UserRole;

public record PlayerDto(String id, String email, String name, UserRole role) {
}
