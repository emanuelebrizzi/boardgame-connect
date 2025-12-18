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

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  profile: UserProfile;
}
