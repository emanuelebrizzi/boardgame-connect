import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../services/auth-service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddGameTableDialog } from '../add-game-table-dialog/add-game-table-dialog';

@Component({
  selector: 'app-association-dashboard',
  imports: [Dashboard, MatButtonModule, MatIconModule],
  templateUrl: './association-dashboard.html',
  styleUrl: './association-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssociationDashboard {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly associationId = computed(() => this.authService.currentUser()?.id);

  addBoardgame() {
    this.router.navigate(['boardgames/add']);
  }

  removeBoardgame() {
    this.router.navigate(['boardgames/remove']);
  }

  addTable() {
    const dialogRef = this.dialog.open(AddGameTableDialog, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((success) => {
      if (success) {
        this.snackBar.open('Table added successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'bottom',
          panelClass: ['success-snackbar'],
        });

        // 2. Refresh logic would go here
        // this.refreshTables.set(Date.now());
      }
    });
  }

  removeTable() {
    this.router.navigate(['tables/remove']);
  }
}
