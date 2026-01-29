import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  input,
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
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { merge } from 'rxjs';
import { ReservationSummary, ReservationFilter, ReservationState } from '../../models/reservation';
import { ReservationService } from '../../services/reservation-service';
import { ReservationCardComponent } from '../reservations/reservation-card/reservation-card';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { extractErrorMessage } from '../../utils/error-handler';

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
    ReservationCardComponent,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard implements OnInit {
  private readonly service = inject(ReservationService);
  private readonly destroyRef = inject(DestroyRef);

  reservations = signal<ReservationSummary[]>([]);
  associationId = input<string>();
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly hasError = computed(() => this.errorMessage() !== '');

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
      this.statusFilter.valueChanges,
    )
      .pipe(
        debounceTime(800), // Wait for user to stop typing
        distinctUntilChanged(), // Ignore identical subsequent values
        takeUntilDestroyed(this.destroyRef), // Auto-unsubscribe
      )
      .subscribe(() => {
        this.loadData();
      });
  }

  loadData() {
    this.isLoading.set(true);
    this.errorMessage.set('');

    const rawState = this.statusFilter.value;
    const stateFilter = rawState ? (rawState as ReservationState) : undefined;
    const effectiveAssociationId = this.associationId() ?? this.assocFilter.value ?? '';

    const filters: ReservationFilter = {
      game: this.nameFilter.value ?? '',
      association: effectiveAssociationId,
      state: stateFilter,
    };

    this.service.getReservations(filters).subscribe({
      next: (data) => {
        this.isLoading.set(false);
        this.reservations.set(data);
      },

      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(extractErrorMessage(err));
      },
    });
  }

  clearFilters() {
    this.nameFilter.setValue('');
    if (!this.associationId()) {
      this.assocFilter.setValue('');
    }
    this.statusFilter.setValue('');
  }
}
