export interface ReservationInfo {
  id: string;
  game: string;
  association: AssociationInfo;
  players: PlayerInfo[];
  minPlayers: number;
  maxPlayers: number;
  startTime: string;
  endTime: string;
}

interface AssociationInfo {
  id: string;
  name: string;
  address: string;
}

interface PlayerInfo {
  id: string;
  username: string;
}
