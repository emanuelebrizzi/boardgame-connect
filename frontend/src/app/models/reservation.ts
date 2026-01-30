import { Player } from './user';
import { AssociationSummary } from './associaton';

export interface Reservation {
  id: string;
  game: string;
  gameImgPath: string;
  association: AssociationSummary;
  players: PlayerSummary[];
  minPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
  state: ReservationState;
}

export enum ReservationState {
  Open = 'open',
  Closed = 'closed',
}

export interface ReservationSummary {
  id: string;
  game: string;
  gameImgPath: string;
  association: string;
  currentPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
  state: ReservationState;
}

export interface ReservationFilter {
  game?: string;
  association?: string;
  state?: ReservationState;
}

export interface ReservationCreateRequest {
  boardgameId: string;
  associationId: string;
  selectedPlayers: number;
  startTime: string; // ISO-8601 format (e.g., "2023-10-05T14:30:00Z")
}

interface PlayerSummary {
  id: string;
  name: string;
}
