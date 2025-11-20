import { TestBed } from '@angular/core/testing';

import { ReservationService } from './reservation.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

import { Reservation } from '@features/reservations/models/reservation';

describe('Reservation', () => {
  let service: ReservationService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReservationService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReservationService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Verify that none of the tests make any extra HTTP requests.
    httpTesting.verify();
  });

  it('getReservations should return the list of reservations on success', async () => {
    const mockReservations: Reservation[] = [
      {
        id: '1',
        boardgameName: 'Root',
        associationName: 'Gilda',
        participantsCurrent: 1,
        participantsMax: 4,
        startTime: '',
        endTime: '',
      },
    ];

    const reservations = service.getReservations();
    const reservationsPromise = firstValueFrom(reservations);
    const req = httpTesting.expectOne(
      { method: 'GET', url: '/api/v1/reservations' },
      'Request to load the reservations'
    );

    req.flush(mockReservations);

    expect(await reservationsPromise).toEqual(mockReservations);
  });
});
