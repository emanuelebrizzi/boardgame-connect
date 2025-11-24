import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { Reservation } from '@features/reservations/models/reservation';
import { ReservationService } from '@features/reservations/services/reservation.service';
import { ReservationComponent } from '../reservation/reservation';

@Component({
  selector: 'app-reservation-list',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatButtonModule,
    ReservationComponent,
  ],
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationListComponent {
  private readonly service = inject(ReservationService);

  reservations = signal<Reservation[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);
  hasError = computed(() => this.error() !== null);

  nameFilter = new FormControl('');
  assocFilter = new FormControl('');
  statusFilter = new FormControl('');

  constructor() {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.error.set(null);

    this.service
      .getReservations()
      .pipe(
        catchError((err) => {
          const msg =
            err.status >= 500
              ? 'Server error. Please try again later.'
              : 'Unable to load reservations. Please check your connection.';

          this.error.set(msg);
          return of([]); // Return empty list so observable doesn't die
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe((data) => {
        if (!this.error()) {
          this.reservations.set(data);
        }
      });
  }
}
