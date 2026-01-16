package boardgameconnect.dto;

import boardgameconnect.model.TableSize;

public record GameTableRequest(int capacity, TableSize size) {
}