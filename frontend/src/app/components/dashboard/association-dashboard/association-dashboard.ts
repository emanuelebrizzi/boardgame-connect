import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-association-dashboard',
  imports: [Dashboard, RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './association-dashboard.html',
  styleUrl: './association-dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssociationDashboard {}
