export interface ReservationDetail {
  id: string;
  game: string;
  association: AssociationDetail;
  players: PlayerDetail[];
  minPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
  state: ReservationState;
}

interface AssociationDetail {
  id: string;
  name: string;
  address: string;
}

interface PlayerDetail {
  id: string;
  username: string;
}

export enum ReservationState {
  Open = 'open',
  Approved = 'approved',
  Closed = 'closed',
}

export interface Reservation {
  id: string;
  game: string;
  associationName: string;
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
