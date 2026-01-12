export interface Reservation {
  id: string;
  game: string;
  associationName: string;
  playersNames: string[];
  minPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
  state: ReservationState;
  coverURL: string;
}

export enum ReservationState {
  Open = 'open',
  Closed = 'closed',
}

export interface ReservationSummary {
  id: string;
  game: string;
  associationName: string;
  currentPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
  state: ReservationState;
  coverURL: string;
}

export interface ReservationFilter {
  game?: string;
  association?: string;
  state?: ReservationState;
}
