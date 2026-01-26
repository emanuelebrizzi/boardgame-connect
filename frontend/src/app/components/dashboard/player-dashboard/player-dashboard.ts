import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AddReservationDialog } from '../add-reservation-dialog/add-reservation-dialog';

@Component({
  selector: 'app-player-dashboard',
  imports: [Dashboard, MatButtonModule, MatIconModule],
  templateUrl: './player-dashboard.html',
  styleUrl: './player-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayerDashboard {
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  addReservation() {
    const dialogRef = this.dialog.open(AddReservationDialog, {
      width: '500px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((reservation) => {
      if (reservation) {
        console.log('Reservation created:', reservation);
        // Redirect logic as requested
        // this.router.navigate(['/lobby', reservation.id]);
      }
    });
  }
}
