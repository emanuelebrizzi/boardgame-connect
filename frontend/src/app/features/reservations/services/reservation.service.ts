import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reservation } from '@features/reservations/models/reservation';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private httpClient = inject(HttpClient);
  private readonly apiURL = '/api/v1/reservations';

  getReservations(): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(this.apiURL);
  }
}
