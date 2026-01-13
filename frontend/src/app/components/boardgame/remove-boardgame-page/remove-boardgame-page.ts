import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Boardgame } from '../../../model/boardgame';
import { BoardgameService } from '../../../services/boardgame-service';
import { extractErrorMessage } from '../../../utils/error-handler';
import { MatButtonModule } from '@angular/material/button';
import { SubmitButton } from '../../submit-button/submit-button';
import { BoardgameSelection } from '../boardgame-selection/boardgame-selection';

@Component({
  selector: 'app-remove-boardgame-page',
  imports: [BoardgameSelection, MatButtonModule, SubmitButton],
  templateUrl: './remove-boardgame-page.html',
  styleUrl: './remove-boardgame-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RemoveBoardgamePage {
  private service = inject(BoardgameService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  myGames = signal<Boardgame[]>([]);
  selectedGameIds = signal<string[]>([]);
  isSubmitting = signal(false);

  ngOnInit() {
    this.service.getMyBoardgames().subscribe((games) => {
      this.myGames.set(games);
    });
  }

  submitRemove() {
    if (this.selectedGameIds().length === 0) return;

    if (!confirm('Are you sure you want to remove these games?')) return;

    this.isSubmitting.set(true);

    this.service.removeBoardgames(this.selectedGameIds()).subscribe({
      next: () => {
        this.snackBar.open('Games removed successfully.', 'Close', { duration: 3000 });
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.snackBar.open(`Error removing games: ${extractErrorMessage(err)}`, 'Close', {
          duration: 3000,
        });
        this.isSubmitting.set(false);
      },
    });
  }

  cancel() {
    this.router.navigate(['/dashboard']);
  }
}
