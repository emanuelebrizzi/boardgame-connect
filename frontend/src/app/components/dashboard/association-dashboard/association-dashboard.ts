import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { Dashboard } from '../dashboard';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../services/auth-service';
import { Router } from '@angular/router';

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

  readonly associationId = computed(() => this.authService.currentUser()?.id);

  addBoardgame() {
    this.router.navigate(['boardgames/add']);
  }

  removeBoardgame() {
    this.router.navigate(['boardgames/remove']);
  }

  addTable() {
    this.router.navigate(['tables/add']);
  }

  removeTable() {
    this.router.navigate(['tables/remove']);
  }
}
