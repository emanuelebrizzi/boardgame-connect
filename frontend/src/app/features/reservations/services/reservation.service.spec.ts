import { TestBed } from '@angular/core/testing';

import { ReservationService } from './reservation.service';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Reservation, ReservationState } from '@features/reservations/models/reservation';
import { ApiError } from '../models/api-error';

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

  describe('getReservations', () => {
    it('should return the list of reservations on success', () => {
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

      service.getReservations().subscribe({
        next: (reservations) => {
          expect(reservations).toEqual(mockReservations);
        },
        error: (e: HttpErrorResponse) => {
          throw new Error(`Expected success, but got error: ${e.message}`);
        },
      });

      const req = httpTesting.expectOne(
        { method: 'GET', url: '/api/v1/reservations' },
        'Request to load the reservations'
      );
      req.flush(mockReservations);
    });

    it('should handle query parameters error ', () => {
      const mockErrorResponse: ApiError = {
        timestamp: '2025-11-13T15:55:12Z',
        status: 500,
        error: 'Bad Request',
        message: 'Invalid game filter parameters',
        path: '/api/v1/reservations',
      };

      service.getReservations().subscribe({
        next: () => {
          throw new Error('Should have failed with 500 error');
        },
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);

          const backendError = error.error as ApiError;
          expect(backendError.message).toBe('Invalid game filter parameters');
        },
      });

      const req = httpTesting.expectOne({ method: 'GET', url: '/api/v1/reservations' });
      req.flush(mockErrorResponse, { status: 500, statusText: 'Server Error' });
    });

    it('should include query parameters in the request when filters are provided', () => {
      const filters = {
        state: ReservationState.Open,
        game: 'Root',
        association: 'Gilda',
      };

      const mockReservations: Reservation[] = [];

      service.getReservations(filters).subscribe({
        next: () => {},
        error: (e) => {
          throw new Error(`Expected success, but got error: ${e.message}`);
        },
      });

      const req = httpTesting.expectOne((req) => {
        return (
          req.url === '/api/v1/reservations' &&
          req.method === 'GET' &&
          req.params.get('state') === 'open' &&
          req.params.get('game') === 'Root' &&
          req.params.get('association') === 'Gilda'
        );
      });

      req.flush(mockReservations);
    });
  });
});
