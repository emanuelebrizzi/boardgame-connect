import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoginRequest, LoginResponse } from '../model/login';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { AssociationRegisterRequest, PlayerRegisterRequest } from '../model/registration';
import { UserProfile, UserRole } from '../model/user';
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

    return this.http.post<LoginResponse>(endpoint, credentials).pipe(
      tap((response) => this.handleAuthSuccess(response)),
      catchError(this.handleError)
    );
  }

  register(
    role: UserRole,
    request: PlayerRegisterRequest | AssociationRegisterRequest
  ): Observable<void> {
    const endpoint =
      role === 'PLAYER'
        ? `${this.API_URL}/register/player`
        : `${this.API_URL}/register/association`;

    return this.http.post<void>(endpoint, request).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.currentUser.set(null);
    this.token.set(null);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
  }

  private getUserFromStorage(): UserProfile | null {
    const storedUser = localStorage.getItem(this.USER_KEY);
    return storedUser ? JSON.parse(storedUser) : null;
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

  private handleError(error: HttpErrorResponse): Observable<never> {
    const message = error.error?.message || 'An unexpected error occurred';
    throw new Error(message);
  }
}
