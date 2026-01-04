export type UserRole = 'PLAYER' | 'ASSOCIATION';

export interface UserAccount {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export interface Player extends UserAccount {}

export interface Association extends UserAccount {
  taxCode: string;
  address: string;
}

export type UserProfile = Player | Association;
