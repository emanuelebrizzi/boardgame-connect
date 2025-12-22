package boardgameconnect.dto;

import boardgameconnect.model.UserRole;

public record PlayerProfile(String id, String email, String name, UserRole role) {
}
