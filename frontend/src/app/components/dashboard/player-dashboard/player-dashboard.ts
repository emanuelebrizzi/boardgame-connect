import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { AddReservationDialog } from '../../reservations/add-reservation-dialog/add-reservation-dialog';

@Component({
  selector: 'app-player-dashboard',
  imports: [Dashboard, MatButtonModule, MatIconModule],
  templateUrl: './player-dashboard.html',
  styleUrl: './player-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayerDashboard {
  private readonly dialog = inject(MatDialog);

  addReservation() {
    this.dialog.open(AddReservationDialog, {
      width: '500px',
      disableClose: true,
    });
  }
}
