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
  state?: string;
  game?: string;
  association?: string;
}
