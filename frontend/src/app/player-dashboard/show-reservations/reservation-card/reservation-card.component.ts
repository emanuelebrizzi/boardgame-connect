import { DatePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Reservation } from '../reservation-models';
import { MatRippleModule } from '@angular/material/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reservation-card',
  imports: [
    MatCardModule,
    MatIcon,
    MatChipsModule,
    MatRippleModule,
    RouterLink,
    UpperCasePipe,
    DatePipe,
  ],
  templateUrl: './reservation-card.html',
  styleUrl: './reservation-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationCardComponent {
  reservation = input.required<Reservation>();

  isFull = computed(() => {
    const r = this.reservation();
    return r.currentPlayers >= r.maxPlayers;
  });
}
