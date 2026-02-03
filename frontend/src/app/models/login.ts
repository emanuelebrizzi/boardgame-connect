import { UserProfile } from './user';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  profile: UserProfile;
}
