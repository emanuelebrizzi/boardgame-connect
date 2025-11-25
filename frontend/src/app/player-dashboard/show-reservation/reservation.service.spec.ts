import { TestBed } from '@angular/core/testing';

import { ReservationService } from './reservation.service';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Reservation, ReservationFilter, ReservationState } from './reservation.model';
import { ApiError } from '../api-error.model';

describe('Reservation', () => {
  let service: ReservationService;
  let httpTesting: HttpTestingController;

  const fail = (description: string) => {
    throw new Error(description);
  };

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

  describe('getReservations', () => {
    it('should return the list of reservations on success', () => {
      const mockReservations: Reservation[] = [
        {
          id: '1',
          game: 'Root',
          association: 'Gilda',
          currentPlayers: 1,
          maxPlayers: 4,
          startTime: '',
          endTime: '',
        },
        {
          id: '2',
          game: 'Root2',
          association: 'Gilda2',
          currentPlayers: 2,
          maxPlayers: 6,
          startTime: '',
          endTime: '',
        },
      ];

      service.getReservations().subscribe({
        next: (reservations) => {
          expect(reservations).toEqual(mockReservations);
        },
        error: (e: HttpErrorResponse) => {
          fail(`Expected success, but got error: ${e.message}`);
        },
      });

      const req = httpTesting.expectOne(
        { method: 'GET', url: `${service.apiURL}/reservations` },
        'Request to load the reservations'
      );
      req.flush(mockReservations);
    });

    it('should fail with 500 for server errors', () => {
      const mockErrorResponse: ApiError = {
        timestamp: '2025-11-13T15:55:12Z',
        status: 500,
        error: 'Internal Server Error',
        message: 'Server is not responding correctly',
        path: 'api/v1/reservations',
      };

      service.getReservations().subscribe({
        next: () => {
          fail('Should have failed with 500 error');
        },
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);

          const backendError = error.error as ApiError;
          expect(backendError.message).toBe('Server is not responding correctly');
        },
      });

      const req = httpTesting.expectOne({ method: 'GET', url: `${service.apiURL}/reservations` });
      req.flush(mockErrorResponse, { status: 500, statusText: 'Server Error' });
    });

    it('should include query parameters in the request when filters are provided', () => {
      const mockReservations: Reservation[] = [];
      const filters = {
        state: ReservationState.Open,
        game: 'Root',
        association: 'Gilda',
      };

      service.getReservations(filters).subscribe({
        next: () => {},
        error: (e) => {
          fail(`Expected success, but got error: ${e.message}`);
        },
      });

      const req = httpTesting.expectOne((req) => {
        return (
          req.url === `${service.apiURL}/reservations` &&
          req.method === 'GET' &&
          req.params.get('state') === 'open' &&
          req.params.get('game') === 'Root' &&
          req.params.get('association') === 'Gilda'
        );
      });

      req.flush(mockReservations);
    });

    it('should return 400 when invalid state is provided', () => {
      // Manually override the compiler error
      const invalidFilter = { state: 'wrong-state' } as unknown as ReservationFilter;

      const mockErrorResponse: ApiError = {
        timestamp: '2025-11-21T10:00:00Z',
        status: 400,
        error: 'Bad Request',
        message: 'Invalid state value',
        path: '/api/v1/reservations',
      };

      service.getReservations(invalidFilter).subscribe({
        next: () => fail('Expected error 400'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect((error.error as ApiError).message).toContain('Invalid state value');
        },
      });

      const req = httpTesting.expectOne((req) => {
        return (
          req.url === `${service.apiURL}/reservations` &&
          req.method === 'GET' &&
          req.params.get('state') === 'wrong-state'
        );
      });

      req.flush(mockErrorResponse, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('getReservation', () => {
    it('should return 200', () => {
      const reservationId: string = '1';

      service.getReservation(reservationId).subscribe({
        error: (e: HttpErrorResponse) => {
          fail(`Expected success, but got error: ${e.message}`);
        },
      });

      const req = httpTesting.expectOne(
        { method: 'GET', url: `${service.apiURL}/reservation/${reservationId}` },
        `Request to load the reservation with id:${reservationId}`
      );

      // req.flush('success');
    });
  });
});
