export interface Boardgame {
  id: string;
  name: string;
  minPlayers: number;
  maxPlayers: number;
  minutesPerPlayer: number;
  coverUrl: string;
}
