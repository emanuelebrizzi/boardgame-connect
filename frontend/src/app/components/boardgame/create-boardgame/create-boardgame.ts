import { Boardgame } from './../../../model/boardgame';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { BoardgameService } from '../../../services/boardgame-service';
import { Router, RouterLink } from '@angular/router';
import { ErrorResponse } from '../../../model/error';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

const minMaxValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const min = control.get('minPlayers')?.value;
  const max = control.get('maxPlayers')?.value;

  if (min !== null && max !== null && min > max) {
    return { minGreaterThanMax: true };
  }
  return null;
};

@Component({
  selector: 'app-create-boardgame',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './create-boardgame.html',
  styleUrl: './create-boardgame.scss',
})
export class CreateBoardgame {
  private fb = inject(FormBuilder);
  private boardgameService = inject(BoardgameService);
  private router = inject(Router);

  errorMessage = signal<string | null>(null);
  isSubmitting = signal<boolean>(false);

  form = this.fb.group(
    {
      name: ['', [Validators.required, Validators.minLength(2)]],
      minPlayers: [2, [Validators.required, Validators.min(1)]],
      maxPlayers: [4, [Validators.required, Validators.min(1)]],
      minutesPerPlayer: [30, [Validators.required, Validators.min(5)]],
      coverUrl: ['', [Validators.required, Validators.pattern(/https?:\/\/.+/)]],
    },
    { validators: minMaxValidator }
  );

  processFormSubmission() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const formValues = this.form.getRawValue();

    const newGame: Boardgame = {
      name: String(formValues.name),
      minPlayers: Number(formValues.minPlayers),
      maxPlayers: Number(formValues.maxPlayers),
      minutesPerPlayer: Number(formValues.minutesPerPlayer),
      coverUrl: String(formValues.coverUrl),
    };

    this.boardgameService.createBoardgame(newGame).subscribe({
      next: () => {
        this.router.navigate(['/dashboard/association'], {
          queryParams: { gameCreated: 'true' },
        });
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const apiError = err.error as ErrorResponse;
        if (apiError?.message) {
          this.errorMessage.set(apiError.message);
        }

        if (err.status === 409 || err.error?.message?.includes('exists')) {
          this.errorMessage.set('Already existing boardgame.');
        } else {
          this.errorMessage.set('Unexpected error during the creation. Try again later.');
        }
      },
    });
  }
}
