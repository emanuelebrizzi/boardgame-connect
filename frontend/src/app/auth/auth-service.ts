import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRequest, LoginResponse, User } from './auth-models';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly USER_KEY = 'user_session';
  private readonly TOKEN_KEY = 'access_token';
  private readonly HOME_PATH = '/';
  private readonly LOGIN_PATH = '/login';

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly apiUrl = 'http://localhost:3000';

  readonly currentUser = signal<User | null>(this.getUserFromStorage());
  readonly token = signal<string | null>(this.getTokenFromStorage());

  readonly isAuthenticated = computed(() => !!this.currentUser() && !!this.token());

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(tap((response) => this.handleAuthSuccess(response)));
  }

  logout(): void {
    this.currentUser.set(null);
    this.token.set(null);

    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);

    this.router.navigate([this.LOGIN_PATH]);
  }

  private getUserFromStorage(): User | null {
    const storedUser = localStorage.getItem(this.USER_KEY);
    return storedUser ? JSON.parse(storedUser) : null;
  }

  private getTokenFromStorage(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private handleAuthSuccess(response: LoginResponse): void {
    const { user, accessToken } = response;

    this.currentUser.set(user);
    this.token.set(accessToken);

    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    localStorage.setItem(this.TOKEN_KEY, accessToken);

    this.router.navigate([this.HOME_PATH]);
  }
}
