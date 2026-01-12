import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ReservationSummary, Reservation, ReservationFilter } from '../model/reservation';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly API_URL = '/api/v1';

  private http = inject(HttpClient);

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
        params = params.set('association.name', filters.association);
      }
    }

    return this.http.get<ReservationSummary[]>(`${this.API_URL}/reservations`, { params });
  }

  getReservation(id: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.API_URL}/reservations/${id}`);
  }
}
