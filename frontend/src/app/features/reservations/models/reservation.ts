export interface Reservation {
  id: string;
  boardgameName: string;
  associationName: string;
  participantsCurrent: number;
  participantsMax: number;
  startTime: string;
  endTime: string;
}

export interface ReservationFilter {
  state?: ReservationState;
  game?: string;
  association?: string;
}

export enum ReservationState {
  Open = 'open',
  Closed = 'closed',
}
