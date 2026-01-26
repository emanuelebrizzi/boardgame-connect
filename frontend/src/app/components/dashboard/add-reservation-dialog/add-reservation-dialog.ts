import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ReservationCreateRequest } from '../../../model/reservation';
import { BoardgameService } from '../../../services/boardgame-service';
import { ReservationService } from '../../../services/reservation-service';
import { AssociationService } from '../../../services/association-service';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

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
  ],
  templateUrl: './add-reservation-dialog.html',
  styleUrl: './add-reservation-dialog.scss',
})
export class AddReservationDialog {
  private readonly fb = inject(FormBuilder);
  private readonly reservationService = inject(ReservationService);
  private readonly associationService = inject(AssociationService);
  private readonly boardgameService = inject(BoardgameService);
  private readonly dialogRef = inject(MatDialogRef<AddReservationDialog>);

  readonly isSubmitting = signal(false);

  // Fetch data for dropdowns
  readonly associations = toSignal(this.associationService.getAllAssociations(), {
    initialValue: [],
  });
  readonly boardgames = toSignal(this.boardgameService.getAllBoardgames(), { initialValue: [] });

  readonly form = this.fb.group({
    associationId: ['', Validators.required],
    boardgameId: ['', Validators.required],
    selectedPlayers: [2, [Validators.required, Validators.min(2)]],
    date: [new Date(), Validators.required],
    time: ['19:00', Validators.required], // Default time
  });

  submit() {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);

    const val = this.form.value;

    // Combine Date and Time into an Instant (ISO String)
    const dateObj = val.date as Date;
    const timeStr = val.time as string;

    // Create a new date object with the selected time
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
      next: (reservation) => {
        this.dialogRef.close(reservation);
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting.set(false);
        // Optional: Add logic to show error message on UI
      },
    });
  }
}
