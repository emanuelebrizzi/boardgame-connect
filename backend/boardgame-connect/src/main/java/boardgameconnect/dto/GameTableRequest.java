package boardgameconnect.dto;

import boardgameconnect.model.GameTableSize;

public record GameTableRequest(int capacity, GameTableSize size) {
}