export enum GameTableSize {
  SMALL = 'SMALL',
  MEDIUM = 'MEDIUM',
  LARGE = 'LARGE',
}

export interface GameTable {
  id: string;
  capacity: number;
  size: GameTableSize;
}

export interface GameTableRequest {
  capacity: number;
  size: GameTableSize;
}
