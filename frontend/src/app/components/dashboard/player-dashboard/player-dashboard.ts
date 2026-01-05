import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Dashboard } from '../dashboard';

@Component({
  selector: 'app-player-dashboard',
  imports: [Dashboard],
  templateUrl: './player-dashboard.html',
  styleUrl: './player-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayerDashboard {}
