import { DatePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Reservation } from '../../../../model/reservation';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reservation-card',
  imports: [MatCardModule, MatIcon, MatChipsModule, RouterLink, UpperCasePipe, DatePipe],
  templateUrl: './reservation-card.html',
  styleUrl: './reservation-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReservationCardComponent {
  readonly imageFolderPath = '/games';

  reservation = input.required<Reservation>();

  // We assume that the name of the image follows the convention: dune-imperium.jpg
  protected gameImageUrl = computed(() => {
    const gameName = this.reservation().game;
    const slug = gameName
      .toLowerCase()
      .replace(/:/g, '') // Remove colons
      .replace(/\s+/g, '-') // Replace spaces with hyphens
      .trim();
    return `${this.imageFolderPath}/${slug}.jpg`;
  });

  protected cardStatus = computed(() => {
    return this.reservation().state;
  });
}
