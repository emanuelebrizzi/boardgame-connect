import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reservation, ReservationFilter } from '@features/reservations/models/reservation';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private httpClient = inject(HttpClient);
  private readonly apiURL = 'http://localhost:3000/reservations/';

  getReservations(filters?: ReservationFilter): Observable<Reservation[]> {
    let params = new HttpParams();

    if (filters) {
      if (filters.state) {
        params = params.set('state', filters.state);
      }

      if (filters.game) {
        params = params.set('boardgameName', filters.game);
      }

      if (filters.association) {
        params = params.set('associationName', filters.association);
      }
    }

    return this.httpClient.get<Reservation[]>(this.apiURL, { params });
  }
}
