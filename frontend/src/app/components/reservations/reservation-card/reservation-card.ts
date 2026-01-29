import { DatePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { ReservationSummary } from '../../../models/reservation';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reservation-card',
  imports: [MatCardModule, MatIcon, MatChipsModule, RouterLink, UpperCasePipe, DatePipe],
  templateUrl: './reservation-card.html',
  styleUrl: './reservation-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationCardComponent {
  reservation = input.required<ReservationSummary>();

  protected gameCoverURL = computed(() => this.reservation().gameImgPath);
  // protected cardStatus = computed(() => this.reservation().state);
}
