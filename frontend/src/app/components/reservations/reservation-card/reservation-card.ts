import { DatePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { ReservationState, ReservationSummary } from '../../../models/reservation';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth-service';

@Component({
  selector: 'app-reservation-card',
  imports: [MatCardModule, MatIcon, MatChipsModule, RouterLink, UpperCasePipe, DatePipe],
  templateUrl: './reservation-card.html',
  styleUrl: './reservation-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationCard {
  private authService = inject(AuthService);

  reservation = input.required<ReservationSummary>();

  protected gameCoverURL = computed(() => this.reservation().gameImgPath);
  protected cardStatus = computed(() => this.reservation().status);

  protected isJoined = computed(() => {
    const userId = this.authService.currentUser()?.id;
    return userId ? this.reservation().participantIds?.includes(userId) : false;
  });

  protected isAccessible = computed(
    () => this.reservation().status === ReservationState.Open || this.isJoined(),
  );
}
