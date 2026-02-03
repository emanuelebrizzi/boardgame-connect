import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginRequest, LoginResponse } from '../models/login';
import { Observable, tap } from 'rxjs';
import { AssociationRegisterRequest, PlayerRegisterRequest } from '../models/registration';
import { UserProfile, UserRole } from '../models/user';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly USER_KEY = 'user_session';
  private readonly TOKEN_KEY = 'access_token';
  private readonly API_URL = `${environment.apiUrl}/auth`;

  private readonly http = inject(HttpClient);

  readonly currentUser = signal<UserProfile | null>(this.getUserFromStorage());
  readonly token = signal<string | null>(this.getTokenFromStorage());
  readonly isAuthenticated = computed(() => !!this.currentUser() && !!this.token());

  login(credentials: LoginRequest, role: UserRole): Observable<LoginResponse> {
    const endpoint = `${this.API_URL}/login/${role.toLowerCase()}`;

    return this.http
      .post<LoginResponse>(endpoint, credentials)
      .pipe(tap((response) => this.handleAuthSuccess(response)));
  }

  register(
    role: UserRole,
    request: PlayerRegisterRequest | AssociationRegisterRequest,
  ): Observable<void> {
    const endpoint =
      role === 'PLAYER'
        ? `${this.API_URL}/register/player`
        : `${this.API_URL}/register/association`;

    return this.http.post<void>(endpoint, request);
  }

  logout(): void {
    this.currentUser.set(null);
    this.token.set(null);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
  }

  private getUserFromStorage(): UserProfile | null {
    try {
      const storedUser = localStorage.getItem(this.USER_KEY);
      return storedUser ? JSON.parse(storedUser) : null;
    } catch (e) {
      localStorage.removeItem(this.USER_KEY); // Clean up bad data
      return null;
    }
  }

  private getTokenFromStorage(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private handleAuthSuccess(response: LoginResponse): void {
    this.currentUser.set(response.profile);
    this.token.set(response.accessToken);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.profile));
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
  }
}
