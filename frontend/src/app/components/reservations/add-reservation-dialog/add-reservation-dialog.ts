import { ChangeDetectionStrategy, Component, inject, Signal, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ReservationCreateRequest } from '../../../models/reservation';
import { ReservationService } from '../../../services/reservation-service';
import { AssociationService } from '../../../services/association-service';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { filter, switchMap, tap } from 'rxjs';
import { Boardgame } from '../../../models/boardgame';
import { FormAlert } from '../../form-alert/form-alert';
import { extractErrorMessage } from '../../../utils/error-handler';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-reservation-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormAlert,
  ],
  templateUrl: './add-reservation-dialog.html',
  styleUrl: './add-reservation-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddReservationDialog {
  private readonly fb = inject(FormBuilder);
  private readonly reservationService = inject(ReservationService);
  private readonly associationService = inject(AssociationService);
  private readonly router = inject(Router);
  private readonly dialogRef = inject(MatDialogRef<AddReservationDialog>);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly selectedGame = signal<Boardgame | null>(null);

  readonly form = this.fb.group({
    associationId: ['', Validators.required],
    boardgameId: [{ value: '', disabled: true }, Validators.required],
    selectedPlayers: [2, [Validators.required, playerCountValidator(this.selectedGame)]],
    date: [new Date(), Validators.required],
    time: ['19:00', Validators.required],
  });

  readonly associations = toSignal(this.associationService.getAllAssociations(), {
    initialValue: [],
  });

  readonly boardgames = toSignal(
    this.form.controls.associationId.valueChanges.pipe(
      tap((id) => {
        if (id) {
          this.form.controls.boardgameId.enable();
        } else {
          this.form.controls.boardgameId.disable();
        }
        this.form.controls.boardgameId.setValue('');
        this.selectedGame.set(null);
      }),
      filter((id) => !!id),
      switchMap((id) => this.associationService.getBoardgames(id!)),
    ),
    { initialValue: [] },
  );

  constructor() {
    this.form.controls.boardgameId.valueChanges.pipe(takeUntilDestroyed()).subscribe((gameId) => {
      const game = this.boardgames().find((g) => g.id === gameId);
      this.selectedGame.set(game || null);
      this.form.controls.selectedPlayers.updateValueAndValidity();
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.isSubmitting.set(true);
    const val = this.form.value;

    // Combine Date and Time into an Instant (ISO String)
    const dateObj = val.date as Date;
    const timeStr = val.time as string;
    const [hours, minutes] = timeStr.split(':').map(Number);
    const combinedDate = new Date(dateObj);
    combinedDate.setHours(hours, minutes, 0, 0);

    const request: ReservationCreateRequest = {
      associationId: val.associationId!,
      boardgameId: val.boardgameId!,
      selectedPlayers: val.selectedPlayers!,
      startTime: combinedDate.toISOString(), // Sends '2023-10-05T19:00:00.000Z'
    };

    this.reservationService.createReservation(request).subscribe({
      next: (reservationId) => {
        this.router.navigate(['/reservations', reservationId]);
        this.dialogRef.close();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(extractErrorMessage(err));
      },
    });
  }
}

export function playerCountValidator(gameSignal: Signal<Boardgame | null>): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const game = gameSignal();
    const value = control.value;

    // If no game is selected yet, or no value entered, rely on 'Validators.required'
    if (!game || value === null || value === undefined) {
      return null;
    }

    if (value < game.minPlayers) {
      return { minPlayers: { required: game.minPlayers, actual: value } };
    }
    if (value > game.maxPlayers) {
      return { maxPlayers: { required: game.maxPlayers, actual: value } };
    }
    return null;
  };
}
