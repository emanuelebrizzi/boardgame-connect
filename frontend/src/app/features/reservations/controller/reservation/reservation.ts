import { Reservation } from '@features/reservations/models/reservation';
import { DatePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-reservation',
  standalone: true,
  imports: [MatCardModule, MatIcon, MatChipsModule, UpperCasePipe, DatePipe],
  templateUrl: './reservation.html',
  styleUrl: './reservation.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationComponent {
  reservation = input.required<Reservation>();

  isFull = computed(() => {
    const r = this.reservation();
    return r.participantsCurrent >= r.participantsMax;
  });
}
