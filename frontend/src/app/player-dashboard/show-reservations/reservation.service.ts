import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Reservation, ReservationDetail, ReservationFilter } from './reservation-models';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  readonly apiURL = 'http://localhost:3000';

  private http = inject(HttpClient);

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

    return this.http.get<ReservationDetail[]>(`${this.apiURL}/reservations`).pipe(
      map((reservations) =>
        reservations.map((r) => ({
          id: r.id,
          game: r.game,
          associationName: r.association.name,
          currentPlayers: r.players.length,
          maxPlayers: r.maxPlayers,
          startTime: r.startTime,
          endTime: r.endTime,
          state: r.state,
        }))
      )
    );
  }

  getReservation(id: string): Observable<ReservationDetail> {
    return this.http.get<ReservationDetail>(`${this.apiURL}/reservations/${id}`);
  }
}
