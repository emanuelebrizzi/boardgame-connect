import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Reservation } from '@features/reservations/models/reservation';
import { ReservationService } from '@features/reservations/services/reservation.service';
import { ReservationComponent } from '../reservation/reservation';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-reservation-list',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    ReservationComponent,
  ],
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationList implements OnInit {
  private readonly service = inject(ReservationService);

  reservations = signal<Reservation[]>([]);
  isLoading = signal(false);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);

    this.service.getReservations().subscribe((data) => {
      this.reservations.set(data);
      this.isLoading.set(false);
    });
  }
}
