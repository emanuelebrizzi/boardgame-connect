import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-submit-button',
  imports: [MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './submit-button.html',
  styleUrl: './submit-button.scss',
})
export class SubmitButton {
  readonly isLoading = input.required<boolean>();
  readonly isDisabled = input<boolean>(false);
  readonly loadingText = input<string>('Processing...');
}
