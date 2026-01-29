import { ReservationService } from '../../../services/reservation-service';
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
import { AuthService } from '../../../services/auth-service';
import { MatSnackBar } from '@angular/material/snack-bar';

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
  private readonly snackBar = inject(MatSnackBar);

  id = input.required<string>();
  readonly currentUser = computed(() => this.authService.currentUser());
  readonly currentUserId = computed(() => this.currentUser()?.id);
  readonly isPlayer = computed(() => this.currentUser()?.role === 'PLAYER');
  readonly reservation = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.reservationService.getReservation(id))),
  );
  readonly isParticipant = computed(() => {
    const res = this.reservation();
    const uid = this.currentUserId();
    return res && uid && res.players.some((p) => p.id === uid);
  });

  joinReservation() {
    this.reservationService.joinReservation(this.id()).subscribe({
      next: () => {
        window.location.reload();
      },
      error: (err) => {
        console.error('Error joining reservation', err);
        this.showSnackBar('Failed to join the reservation. Please try again.', 'error');
      },
    });
  }

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
        this.showSnackBar('You have left the reservation.');
        this.router.navigate(['/dashboard/player'], { replaceUrl: true });
      },
      error: (err) => {
        console.error('Error leaving reservation', err);
        this.showSnackBar('Could not leave the reservation. Please try again.', 'error');
      },
    });
  }

  private showSnackBar(message: string, type: 'success' | 'error' = 'success') {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: type === 'error' ? ['error-snackbar'] : ['success-snackbar'],
    });
  }
}
