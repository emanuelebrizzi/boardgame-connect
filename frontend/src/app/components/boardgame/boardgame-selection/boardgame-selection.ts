import { ChangeDetectionStrategy, Component, input, model } from '@angular/core';
import { Boardgame } from '../../../model/boardgame';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-boardgame-selection',
  imports: [MatCardModule, MatIconModule, MatCheckboxModule, NgClass],
  templateUrl: './boardgame-selection.html',
  styleUrl: './boardgame-selection.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BoardgameSelection {
  games = input.required<Boardgame[]>();

  selectedIds = model<string[]>([]);

  isSelected(game: Boardgame): boolean {
    return this.selectedIds().includes(game.id);
  }

  toggleSelection(game: Boardgame) {
    const current = this.selectedIds();
    if (current.includes(game.id)) {
      this.selectedIds.set(current.filter((id) => id !== game.id));
    } else {
      this.selectedIds.set([...current, game.id]);
    }
  }
}
