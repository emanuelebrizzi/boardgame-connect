import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { GameTableRequest, GameTableSize } from '../../../models/game-table';
import { AssociationService } from '../../../services/association-service';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-add-game-table-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './add-game-table-dialog.html',
  styleUrl: './add-game-table-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddGameTableDialog {
  private readonly fb = inject(FormBuilder);
  private readonly associationService = inject(AssociationService);
  private readonly dialogRef = inject(MatDialogRef<AddGameTableDialog>);

  readonly tableSizes = Object.values(GameTableSize);
  isSubmitting = false;

  readonly form = this.fb.group({
    capacity: [4, [Validators.required, Validators.min(1)]],
    size: [GameTableSize.MEDIUM, [Validators.required]],
  });

  submit() {
    if (this.form.invalid) return;

    this.isSubmitting = true;
    const request = this.form.value as GameTableRequest;

    this.associationService.addTable(request).subscribe({
      next: () => this.dialogRef.close(true),
      error: (error) => {
        this.isSubmitting = false;
        console.error(error);
      },
    });
  }
}
