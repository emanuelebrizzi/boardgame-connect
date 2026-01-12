import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-player-dashboard',
  imports: [Dashboard, MatButtonModule, MatIconModule],
  templateUrl: './player-dashboard.html',
  styleUrl: './player-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayerDashboard {}
