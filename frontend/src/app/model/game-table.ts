export enum GameTableSize {
  SMALL = 'SMALL',
  MEDIUM = 'MEDIUM',
  LARGE = 'LARGE',
}

export interface GameTableRequest {
  capacity: number;
  size: GameTableSize;
}
