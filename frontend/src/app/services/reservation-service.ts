import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import {
  ReservationSummary,
  Reservation,
  ReservationFilter,
  ReservationCreateRequest,
} from '../models/reservation';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/reservations`;

  getReservations(filters?: ReservationFilter): Observable<ReservationSummary[]> {
    let params = new HttpParams();

    if (filters) {
      if (filters.state) {
        params = params.set('state', filters.state);
      }

      if (filters.game) {
        params = params.set('game', filters.game);
      }

      if (filters.association) {
        params = params.set('association', filters.association);
      }
    }

    return this.http.get<ReservationSummary[]>(`${this.apiUrl}`, { params });
  }

  getReservation(id: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/${id}`);
  }

  // It returns the id of the created reservation to the caller.
  createReservation(request: ReservationCreateRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}`, request);
  }

  joinReservation(reservationId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${reservationId}/join`, {});
  }

  leaveReservation(reservationId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reservationId}/leave`);
  }
}
