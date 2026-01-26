package boardgameconnect.dto;

import boardgameconnect.model.GameTableSize;

public record GameTableResponse(String id, GameTableSize size, int capacity) {
}