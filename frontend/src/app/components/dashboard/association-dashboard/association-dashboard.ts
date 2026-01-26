import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../services/auth-service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddGameTableDialog } from '../add-game-table-dialog/add-game-table-dialog';
import { MatTableModule } from '@angular/material/table';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { AssociationService } from '../../../services/association-service';
import { GameTable } from '../../../model/game-table';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-association-dashboard',
  imports: [Dashboard, MatButtonModule, MatIconModule, MatTableModule, MatCardModule],
  templateUrl: './association-dashboard.html',
  styleUrl: './association-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssociationDashboard {
  private readonly authService = inject(AuthService);
  private readonly associationService = inject(AssociationService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly associationId = computed(() => this.authService.currentUser()?.id);

  readonly displayedColumns: string[] = ['size', 'capacity', 'actions'];

  // Signal to trigger table refreshes
  private readonly refreshTrigger = signal(0);

  // Automatically fetch tables whenever refreshTrigger changes
  readonly tables = toSignal(
    toObservable(this.refreshTrigger).pipe(switchMap(() => this.associationService.getTables())),
    { initialValue: [] as GameTable[] }
  );

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
        this.showSuccess('Table added successfully');
        this.refreshTables();
      }
    });
  }

  removeTable(tableId: string) {
    if (confirm('Are you sure you want to delete this table?')) {
      this.associationService.removeTable(tableId).subscribe({
        next: () => {
          this.showSuccess('Table removed successfully');
          this.refreshTables();
        },
        error: () => this.showError('Could not remove table'),
      });
    }
  }

  private refreshTables() {
    this.refreshTrigger.update((v) => v + 1);
  }

  private showSuccess(message: string) {
    this.snackBar.open(message, 'Close', { duration: 3000 });
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 3000, panelClass: 'error-snackbar' });
  }
}
