import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-error',
  imports: [MatCardModule, MatIconModule, MatButtonModule, RouterLink],
  templateUrl: './error-card.html',
  styleUrl: './error-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorCard {
  readonly code = input<string>('404');
  readonly title = input<string>('Page Not Found');
  readonly message = input<string>('The page you are looking for does not exist.');
}
