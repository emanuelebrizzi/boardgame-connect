export interface Boardgame {
  id: string;
  name: string;
  minPlayers: number;
  maxPlayers: number;
  timeMin: number;
  timePerPlayer: number;
  imagePath: string;
}
