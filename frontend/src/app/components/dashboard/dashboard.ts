import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { merge, of, Subject } from 'rxjs';
import { catchError, debounceTime, switchMap, tap } from 'rxjs/operators';
import { ReservationFilter, ReservationState } from '../../models/reservation';
import { ReservationService } from '../../services/reservation-service';
import { extractErrorMessage } from '../../utils/error-handler';
import { ReservationCard } from '../reservations/reservation-card/reservation-card';

@Component({
  selector: 'app-dashboard',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatButtonModule,
    ReservationCard,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard {
  private readonly service = inject(ReservationService);

  associationId = input<string>();
  nameFilter = new FormControl('');
  assocFilter = new FormControl('');
  statusFilter = new FormControl('');

  private readonly nameQuery = toSignal(this.nameFilter.valueChanges, { initialValue: '' });
  private readonly associationQuery = toSignal(this.assocFilter.valueChanges, { initialValue: '' });
  private readonly statusQuery = toSignal(this.statusFilter.valueChanges, { initialValue: '' });

  private readonly filters = computed<ReservationFilter>(() => ({
    game: this.nameQuery() ?? '',
    association: this.associationId() ?? this.associationQuery() ?? '',
    state: (this.statusQuery() as ReservationState) || undefined,
  }));

  readonly isLoading = signal(true);
  readonly errorMessage = signal('');
  readonly hasError = computed(() => this.errorMessage() !== '');
  private readonly refreshTrigger$ = new Subject<void>();

  readonly reservations = toSignal(
    merge(toObservable(this.filters).pipe(debounceTime(800)), this.refreshTrigger$).pipe(
      tap(() => {
        this.isLoading.set(true);
        this.errorMessage.set('');
      }),
      switchMap(() => {
        return this.service.getReservations(this.filters()).pipe(
          tap(() => this.isLoading.set(false)),
          catchError((err) => {
            this.isLoading.set(false);
            this.errorMessage.set(extractErrorMessage(err));
            return of([]); // Return empty list on error so the stream stays alive
          }),
        );
      }),
    ),
    { initialValue: [] },
  );

  refresh() {
    this.refreshTrigger$.next();
  }

  clearFilters() {
    this.nameFilter.setValue('');
    if (!this.associationId()) {
      this.assocFilter.setValue('');
    }
    this.statusFilter.setValue('');
  }
}
