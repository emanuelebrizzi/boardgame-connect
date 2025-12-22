import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { catchError, debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { merge, of } from 'rxjs';
import { Reservation, ReservationFilter, ReservationState } from '../../model/reservation';
import { ReservationService } from '../../services/reservation/reservation-service';
import { ReservationCardComponent } from './show-reservations/reservation-card/reservation-card';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'player-dashboard',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatButtonModule,
    ReservationCardComponent,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  private readonly service = inject(ReservationService);
  private readonly destroyRef = inject(DestroyRef);

  reservations = signal<Reservation[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);
  hasError = computed(() => this.error() !== null);

  nameFilter = new FormControl('');
  assocFilter = new FormControl('');
  statusFilter = new FormControl('');

  ngOnInit(): void {
    this.setupFilterListeners();
    this.loadData();
  }

  private setupFilterListeners() {
    merge(
      this.nameFilter.valueChanges,
      this.assocFilter.valueChanges,
      this.statusFilter.valueChanges
    )
      .pipe(
        debounceTime(800), // Wait for user to stop typing
        distinctUntilChanged(), // Ignore identical subsequent values
        takeUntilDestroyed(this.destroyRef) // Auto-unsubscribe
      )
      .subscribe(() => {
        this.loadData();
      });
  }

  loadData() {
    this.isLoading.set(true);
    this.error.set(null);

    const rawState = this.statusFilter.value;
    const stateFilter = rawState ? (rawState as ReservationState) : undefined;
    const filters: ReservationFilter = {
      game: this.nameFilter.value ?? '',
      association: this.assocFilter.value ?? '',
      state: stateFilter,
    };

    this.service
      .getReservations(filters)
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

  clearFilters() {
    this.nameFilter.setValue('');
    this.assocFilter.setValue('');
    this.statusFilter.setValue('');
  }
}
