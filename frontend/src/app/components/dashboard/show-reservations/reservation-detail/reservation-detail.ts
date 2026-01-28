import { ReservationService } from '../../../../services/reservation-service';
import { DatePipe } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from '../../../../services/auth-service';

@Component({
  selector: 'app-reservation-detail',
  imports: [
    MatCardModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatListModule,
    MatButtonModule,
    RouterLink,
    DatePipe,
  ],
  templateUrl: './reservation-detail.html',
  styleUrl: './reservation-detail.scss',
})
export class ReservationDetail {
  private readonly reservationService = inject(ReservationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  id = input.required<string>();
  readonly currentUserId = computed(() => this.authService.currentUser()?.id);
  readonly reservation = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.reservationService.getReservation(id))),
  );

  readonly isParticipant = computed(() => {
    const res = this.reservation();
    const uid = this.currentUserId();
    return res && uid && res.players.some((p) => p.id === uid);
  });

  exitReservation() {
    if (
      !confirm(
        'Are you sure you want to leave? If you are the last player, the reservation will be cancelled.',
      )
    ) {
      return;
    }

    this.reservationService.leaveReservation(this.id()).subscribe({
      next: () => {
        this.router.navigate(['/dashboard/player'], { replaceUrl: true });
      },
      error: (err) => {
        console.error('Error leaving reservation', err);
        alert('Could not leave the reservation. Please try again.');
      },
    });
  }
}
