import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reservation, ReservationFilter } from './reservation.model';
import { ReservationInfo } from './reservation-info.model';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  readonly apiURL = 'http://localhost:3000';

  private httpClient = inject(HttpClient);

  getReservations(filters?: ReservationFilter): Observable<Reservation[]> {
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

    return this.httpClient.get<Reservation[]>(`${this.apiURL}/reservations`, { params });
  }

  getReservation(id: string): Observable<ReservationInfo> {
    return this.httpClient.get<ReservationInfo>(`${this.apiURL}/reservation/${id}`);
  }
}
