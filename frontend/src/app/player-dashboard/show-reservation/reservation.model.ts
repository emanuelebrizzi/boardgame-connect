export interface Reservation {
  id: string;
  game: string;
  association: string;
  currentPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
}

export interface ReservationFilter {
  game?: string;
  association?: string;
  state?: ReservationState;
}

export enum ReservationState {
  Open = 'open',
  Approved = 'approved',
  Closed = 'closed',
}
