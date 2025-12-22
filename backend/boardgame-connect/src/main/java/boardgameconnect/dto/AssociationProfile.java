package boardgameconnect.dto;

import boardgameconnect.model.UserRole;

public record AssociationProfile(String id, String email, String name, String taxCode, String address, UserRole role) {
}
