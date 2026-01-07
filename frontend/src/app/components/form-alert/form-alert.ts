import { Component, input } from '@angular/core';

@Component({
  selector: 'app-form-alert',
  imports: [],
  templateUrl: './form-alert.html',
  styleUrl: './form-alert.scss',
})
export class FormAlert {
  readonly message = input<string | null>(null);
}
