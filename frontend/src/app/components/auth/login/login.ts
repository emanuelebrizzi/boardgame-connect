import { AuthService } from '../../../services/auth-service';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { UserRole } from '../../../models/user';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { RoleSelector } from '../role-selector/role-selector';
import { SubmitButton } from '../../shared/submit-button/submit-button';
import { FormAlert } from '../../form-alert/form-alert';
import { extractErrorMessage } from '../../../utils/error-handler';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatButtonToggleModule,
    RoleSelector,
    SubmitButton,
    FormAlert,
    RouterLink,
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  private readonly snackBar = inject(MatSnackBar);

  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly role = signal<UserRole>('PLAYER');

  readonly loginForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  processLoginForm() {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const credentials = this.loginForm.getRawValue();
    const role = this.role();

    this.authService.login(credentials, role).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.snackBar.open('Login successful! Welcome back.', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
        });
        this.router.navigate(['/dashboard']);
      },

      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(extractErrorMessage(err));
      },
    });
  }
}
