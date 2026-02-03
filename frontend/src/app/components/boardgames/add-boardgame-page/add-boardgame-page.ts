import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { BoardgameService } from '../../../services/boardgame-service';
import { forkJoin } from 'rxjs';
import { Boardgame } from '../../../models/boardgame';
import { extractErrorMessage } from '../../../utils/error-handler';
import { MatButtonModule } from '@angular/material/button';
import { SubmitButton } from '../../shared/submit-button/submit-button';
import { BoardgameSelection } from '../boardgame-selection/boardgame-selection';

@Component({
  selector: 'app-add-boardgame-page',
  imports: [BoardgameSelection, MatButtonModule, SubmitButton],
  templateUrl: './add-boardgame-page.html',
  styleUrl: './add-boardgame-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddBoardgamePage implements OnInit {
  private service = inject(BoardgameService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  private allGames = signal<Boardgame[]>([]);
  private myGames = signal<Boardgame[]>([]);

  selectedGameIds = signal<string[]>([]);
  isSubmitting = signal(false);

  availableGames = computed(() => {
    const myIds = new Set(this.myGames().map((g) => g.id));
    return this.allGames().filter((g) => !myIds.has(g.id));
  });

  ngOnInit() {
    forkJoin({
      all: this.service.getAllBoardgames(),
      mine: this.service.getMyBoardgames(),
    }).subscribe(({ all, mine }) => {
      this.allGames.set(all);
      this.myGames.set(mine);
    });
  }

  submitAdd() {
    if (this.selectedGameIds().length === 0) return;

    this.isSubmitting.set(true);

    this.service.addBoardgames(this.selectedGameIds()).subscribe({
      next: () => {
        this.snackBar.open('Games added successfully!', 'Close', { duration: 3000 });
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.snackBar.open(`Error adding games: ${extractErrorMessage(err)}`, 'Close', {
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
